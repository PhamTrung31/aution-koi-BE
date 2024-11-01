package swp.auctionkoi.service.auction;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.respone.auction.AuctionCanNotStartInfo;
import swp.auctionkoi.dto.respone.auction.AuctionEndInfo;
import swp.auctionkoi.dto.respone.auction.AuctionPendingInfo;
import swp.auctionkoi.dto.respone.auction.AuctionStartInfo;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.mapper.AuctionRequestMapper;

import swp.auctionkoi.models.Auction;
import swp.auctionkoi.models.AuctionParticipants;
import swp.auctionkoi.models.AuctionRequest;
import swp.auctionkoi.models.Bid;
import swp.auctionkoi.models.enums.AuctionRequestStatus;
import swp.auctionkoi.models.enums.AuctionStatus;
import swp.auctionkoi.models.enums.AuctionType;
import swp.auctionkoi.models.enums.KoiStatus;
import swp.auctionkoi.repository.AuctionParticipantsRepository;
import swp.auctionkoi.repository.AuctionRepository;
import swp.auctionkoi.repository.AuctionRequestRepository;
import swp.auctionkoi.repository.BidRepository;
import swp.auctionkoi.service.AuctionNotificationService;
import swp.auctionkoi.service.auction.AuctionService;
//import swp.auctionkoi.service.bid.BidService;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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
    AuctionNotificationService auctionNotificationService;
    BidRepository bidRepository;

//    ConcurrentMap<Integer, Boolean> auctionNotificationSentMap = new ConcurrentHashMap<>();
    HashMap<Integer, Boolean> auctionNotificationSentMap = new HashMap<>();

    @Scheduled(fixedRate = 1000)
    @Transactional
    public void checkAuctions() {
        log.info("Scheduler running");
        processAuctions();
//        updateCachedAuctionRequests();
    }

    @Transactional
    public void processAuctions() {

        List<AuctionRequest> auctionRequestList = getSortedAuctionRequestsWithAuctionsByStartTime();

        for (AuctionRequest request : auctionRequestList) {
            //get current time
            Instant instantNow = Instant.now();
            // Add 7 hours to Instant
            Instant currentTime = instantNow.plus(Duration.ofHours(7));

            log.info("Current Time: {}", currentTime);
            //get list bidder in this auction
            List<AuctionParticipants> auctionParticipants = auctionParticipantsRepository.findListAuctionParticipantsByAuctionId(request.getAuction().getId());

            if (request.getAuction().getStatus().equals(AuctionStatus.PENDING) && request.getRequestStatus().equals(AuctionRequestStatus.APPROVE)) {

                existAuctionPending(request);

                startAuctionSchedule(request, auctionParticipants, currentTime);
            }
            if(request.getAuction().getStatus().equals(AuctionStatus.IN_PROGRESS)){
                endTimeButWasNotStartSchedule(request, auctionParticipants, currentTime);
                endAuctionSchedule(request, auctionParticipants, currentTime);
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

    private void existAuctionPending(AuctionRequest request) {
        if (!auctionNotificationSentMap.containsKey(request.getAuction().getId()) && request.getAuction().getStatus() == AuctionStatus.PENDING && request.getRequestStatus().equals(AuctionRequestStatus.APPROVE)) {
            AuctionPendingInfo auctionPendingInfo = AuctionPendingInfo.builder()
                    .auction_id(request.getAuction().getId())
                    .fish_id(request.getFish().getId())
                    .fish_name(request.getFish().getName())
                    .fish_size(request.getFish().getSize())
                    .fish_sex(request.getFish().getSex())
                    .fish_age(request.getFish().getAge())
                    .methodType(request.getMethodType())
                    .deposit_amount(request.getAuction().getDepositAmount())
                    .imageUrl(request.getFish().getImageUrl())
                    .videoUrl(request.getFish().getVideoUrl())
                    .start_time(request.getStartTime())
                    .end_time(request.getEndTime())
                    .build();
            auctionNotificationService.sendAuctionPendingNotification(auctionPendingInfo);
            //put to the map for not send again
            auctionNotificationSentMap.put(request.getAuction().getId(), true);
        }
    }

    @Transactional
    public void startAuctionSchedule(AuctionRequest request, List<AuctionParticipants> auctionParticipants, Instant currentTime) {
        if (request.getStartTime().isBefore(currentTime) || request.getStartTime().equals(currentTime)) {
            if (auctionParticipants.size() > 2) { //real will be 7
                // Call the auction start function
                auctionService.startAuction(request.getAuction().getId());
                //build message
                AuctionStartInfo auctionStartInfo = AuctionStartInfo.builder()
                        .auction_id(request.getAuction().getId())
                        .auction_type(request.getMethodType())
                        .fish_id(request.getAuction().getFish().getId())
                        .fish_name(request.getAuction().getFish().getName())
                        .fish_age(request.getAuction().getFish().getAge())
                        .fish_size(request.getAuction().getFish().getSize())
                        .fish_sex(request.getAuction().getFish().getSex())
                        .imageUrl(request.getAuction().getFish().getImageUrl())
                        .videoUrl(request.getAuction().getFish().getVideoUrl())
                        .buy_out(request.getBuyOut())
                        .deposit_amount(request.getAuction().getDepositAmount())
                        .start_time(request.getStartTime())
                        .end_time(request.getEndTime())
                        .method_type(request.getMethodType())
                        .aution_status(request.getAuction().getStatus())
                        .build();
                //send message
                auctionNotificationService.sendAuctionStartNotification(auctionStartInfo);
                log.info("Auction ID: {} started", request.getAuction().getId());
            }
        }
    }

    @Transactional
    public void endTimeButWasNotStartSchedule(AuctionRequest request, List<AuctionParticipants> auctionParticipants, Instant currentTime) {
        //check that this auction have bid and have valid number of participant or not
        if (request.getEndTime().isBefore(currentTime) && auctionParticipants.size() < 5) { //real run will be 7
            request.getAuction().setStatus(AuctionStatus.UNSOLD);
            request.getAuction().setWinner(null);
            request.getFish().setStatus(KoiStatus.UNSOLD);
            auctionRequestRepository.save(request);
            AuctionCanNotStartInfo canNotStartInfo = AuctionCanNotStartInfo.builder()
                    .auction_id(request.getAuction().getId())
                    .message("Auction can not start. An auction must have at least 7 members.")
                    .build();
            auctionNotificationService.sendAuctionCantNotStartNotification(canNotStartInfo);
            log.info("Auction ID: {} marked as UNSOLD due to insufficient participants.", request.getAuction().getId());
        }
    }

    @Transactional
    public void endAuctionSchedule(AuctionRequest request, List<AuctionParticipants> auctionParticipants, Instant currentTime) {
        if (request.getEndTime().isBefore(currentTime) && auctionParticipants.size() > 2) {
            List<Bid> listBid = bidRepository.getListBidByAuctionId(request.getAuction().getId());
            if (!listBid.isEmpty()) {
                //call the end auction function
                auctionService.endAuction(request.getAuction().getId());
                AuctionEndInfo auctionEndInfo;
                if (request.getAuction().getWinner() != null) {
                    auctionEndInfo = AuctionEndInfo.builder()
                            .auction_id(request.getAuction().getId())
                            .user_id(request.getAuction().getWinner().getId())
                            .highest_prices(request.getAuction().getHighestPrice())
                            .build();
                    auctionNotificationService.sendAuctionEndNotification(auctionEndInfo);
                    log.info("Auction ID: {} ended", request.getAuction().getId());
                }
            }
        }
    }
}
