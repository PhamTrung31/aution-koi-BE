package swp.auctionkoi.service.auction;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.Auction;
import swp.auctionkoi.models.AuctionParticipants;
import swp.auctionkoi.models.AuctionRequest;
import swp.auctionkoi.models.Bid;
import swp.auctionkoi.models.enums.AuctionRequestStatus;
import swp.auctionkoi.models.enums.AuctionStatus;
import swp.auctionkoi.models.enums.AuctionType;
import swp.auctionkoi.repository.AuctionParticipantsRepository;
import swp.auctionkoi.repository.AuctionRepository;
import swp.auctionkoi.repository.AuctionRequestRepository;
import swp.auctionkoi.repository.BidRepository;
import swp.auctionkoi.service.auction.AuctionService;
//import swp.auctionkoi.service.bid.BidService;

import java.time.Duration;
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
    AuctionService auctionService;
//    BidService bidService;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkAuctions() {
        log.info("Scheduler running");
        processAuctions();
    }

//    @Scheduled(fixedRate = 60000)
//    @Transactional
//    public void getTop5Traditional(){
//        bidService.updateTop5TraditionalBids();
//    }

    //OffsetDateTime currentTimeInOffset = currentTime.atOffset(ZoneOffset.ofHours(7));

    private void processAuctions() {

        List<AuctionRequest> approvedRequestsSortedByStartTime  = getSortedAuctionRequestsWithAuctionsByStartTime();

        for (AuctionRequest request : approvedRequestsSortedByStartTime) {
            if(request.getAuction().getStatus().equals(AuctionStatus.PENDING) || request.getAuction().getStatus().equals(AuctionStatus.UNSOLD)){
                //get current time
                Instant currentTime = Instant.now();
                // Add 7 hours to Instant
                Instant updatedInstant = currentTime.plus(Duration.ofHours(7));
                log.info("Checking AuctionRequest ID: {}, StartTime: {}, Status: {}",
                        request.getId(),
                        request.getStartTime(),
                        request.getAuction() != null ? request.getAuction().getStatus() : "No Auction");
                //get list bidder in this auction
                List<AuctionParticipants> auctionParticipants = auctionParticipantsRepository.findListAuctionParticipantsByAuctionId(request.getAuction().getId());
                log.info("Number of participants for Auction ID {}: {}",
                        request.getAuction().getId(),
                        auctionParticipants.size());

                log.info("Auction ID {}: is before now {} , ", request.getAuction().getId(), request.getStartTime().isBefore(updatedInstant));
                if (request.getStartTime().isBefore(updatedInstant) || request.getStartTime().equals(updatedInstant)) {
                    // Ensure the auction request status is APPROVED
                    if (request.getRequestStatus().equals(AuctionRequestStatus.APPROVE) && request.getAuction().getStatus().equals(AuctionStatus.PENDING)) {
                        // Mark the auction as started
                        auctionService.startAuction(request.getAuction().getId());
                        log.info("Auction ID: {} started", request.getAuction().getId());
                    } else {
                        log.info("Auction ID: {} cannot start yet. Status: {}", request.getAuction().getId(), request.getAuction().getStatus());
                    }
                } else {
                    log.info("Auction ID: {} cannot start yet. CurrentTime: {}, StartTime: {}",
                            request.getAuction().getId(), updatedInstant, request.getStartTime());
                }
            }
        }

        List<AuctionRequest> approvedRequestsSortedByEndTime = getSortedAuctionRequestsWithAuctionsByEndTime();

        endTimeButWasNotStart(approvedRequestsSortedByEndTime);

        for (AuctionRequest request : approvedRequestsSortedByEndTime) {
            if(request.getAuction().getStatus().equals(AuctionStatus.IN_PROGRESS) || request.getAuction().getStatus().equals(AuctionStatus.BUYOUT_TRIGGERED)){
                //get current time
                Instant currentTime = Instant.now();
                // Add 7 hours to Instant
                Instant updatedInstant = currentTime.plus(Duration.ofHours(7));
                log.info("Checking AuctionRequest ID: {}, EndTime: {}, Status: {}",
                        request.getId(),
                        request.getEndTime(),
                        request.getAuction() != null ? request.getAuction().getStatus() : "No Auction");

                if (request.getEndTime().isBefore(updatedInstant)) {

                    log.info("Ending Auction ID: {}, Winner: {}, Highest Price: {}",
                            request.getAuction().getId(),
                            request.getAuction().getWinner(),
                            request.getAuction().getHighestPrice());

                    auctionService.endAuction(request.getAuction().getId());

                    log.info("Auction ID: {} ended", request.getAuction().getId());
                } else {
                    // Log why the auction didn't end
                    log.info("Auction ID: {} not ended - either not in progress, no winner, or not past end time",
                            request.getAuction().getId());
                }
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


    private void endTimeButWasNotStart(List<AuctionRequest> approvedRequestsSortedByEndTime){
        for (AuctionRequest request : approvedRequestsSortedByEndTime) {
            //get current time
            Instant currentTime = Instant.now();
            // Add 7 hours to Instant
            Instant updatedInstant = currentTime.plus(Duration.ofHours(7));
            List<AuctionParticipants> auctionParticipants = auctionParticipantsRepository.findListAuctionParticipantsByAuctionId(request.getAuction().getId());
            log.info("Checking AuctionRequest ID: {}, EndTime: {}, Status: {}, Number of Participants: {}",
                    request.getId(),
                    request.getEndTime(),
                    request.getRequestStatus(),
                    auctionParticipants.size());

            if (request.getRequestStatus().equals(AuctionRequestStatus.APPROVE) && request.getEndTime().isBefore(updatedInstant)
                    && request.getAuction() != null && request.getAuction().getStatus().equals(AuctionStatus.PENDING)
                    && auctionParticipants.size() > 2){ //real run will be 7


                request.getAuction().setStatus(AuctionStatus.UNSOLD);
                request.getAuction().setWinner(null);
                auctionRequestRepository.save(request);
                log.info("Auction ID: {} marked as UNSOLD due to insufficient participants.", request.getAuction().getId());
            } else {
                // Log why this auction request was not marked as unsold
                log.info("Auction ID: {} not marked as UNSOLD. Conditions not met: Approved={}, EndTime={}, Status={}, Participants={}",
                        request.getAuction().getId(),
                        request.getRequestStatus(),
                        request.getEndTime(),
                        request.getAuction().getStatus(),
                        auctionParticipants.size());
            }
        }
    }
}
