package swp.auctionkoi.service.auction;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.Auction;
import swp.auctionkoi.models.AuctionParticipants;
import swp.auctionkoi.models.AuctionRequest;
import swp.auctionkoi.models.enums.AuctionStatus;
import swp.auctionkoi.repository.AuctionParticipantsRepository;
import swp.auctionkoi.repository.AuctionRepository;
import swp.auctionkoi.repository.AuctionRequestRepository;
import swp.auctionkoi.service.auction.impl.AuctionServiceImpl;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuctionScheduler {

    AuctionRepository auctionRepository;
    AuctionRequestRepository auctionRequestRepository;
    AuctionParticipantsRepository auctionParticipantsRepository;
    AuctionServiceImpl auctionServiceImpl;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkAuctions() {
        log.info("Scheduler running");
        processAuctions();
    }

    private void processAuctions() {
        //get current time
        Instant currentTime = Instant.now();

        List<AuctionRequest> approvedRequestsSortedByStartTime  = getSortedAuctionRequestsWithAuctionsByStartTime();

        for (AuctionRequest request : approvedRequestsSortedByStartTime) {
            List<AuctionParticipants> auctionParticipants = auctionParticipantsRepository.findListAuctionParticipantsByAuctionId(request.getAuction().getId());
            if (request.getAuction() != null && auctionParticipants.size() > 2) {
                auctionServiceImpl.startAuction(request.getAuction().getId());
                log.info("Auction {} started", request.getAuction().getId());
            }
        }

        List<AuctionRequest> approvedRequestsSortedByEndTime = getSortedAuctionRequestsWithAuctionsByEndTime();

        for (AuctionRequest request : approvedRequestsSortedByEndTime) {
            if (request.getEndTime().isBefore(currentTime) && request.getAuction() != null
                    && request.getAuction().getStatus().equals(AuctionStatus.IN_PROGRESS)
                    && request.getAuction().getWinner() != null && request.getAuction().getHighestPrice() != null
                    && request.getAuction().getCurrentPrice() != null) {

                auctionServiceImpl.endAuction(request.getAuction().getId());
            }
        }
    }

    private List<AuctionRequest> getSortedAuctionRequestsWithAuctionsByStartTime() {
        List<AuctionRequest> auctionRequests = auctionRequestRepository.findByAuctionIsNotNull();
        return auctionRequests.stream()
                .filter(ar -> ar.getStartTime() != null)  //start item in list have startTime is null
                .sorted(Comparator.comparing(AuctionRequest::getStartTime))  //sort by startTime
                .collect(Collectors.toList());
    }

    private List<AuctionRequest> getSortedAuctionRequestsWithAuctionsByEndTime() {
        List<AuctionRequest> auctionRequests = auctionRequestRepository.findByAuctionIsNotNull();
        return auctionRequests.stream()
                .filter(ar -> ar.getEndTime() != null)  //remove item in list have endTime is null
                .sorted(Comparator.comparing(AuctionRequest::getEndTime))  //sort by endTime
                .collect(Collectors.toList());
    }
}
