//package swp.auctionkoi.service.bid.impl;
//
//import jakarta.transaction.Transactional;
//import lombok.AccessLevel;
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import swp.auctionkoi.exception.AppException;
//import swp.auctionkoi.exception.ErrorCode;
//import swp.auctionkoi.models.*;
//import swp.auctionkoi.models.enums.AuctionRequestStatus;
//import swp.auctionkoi.models.enums.AuctionStatus;
//import swp.auctionkoi.models.enums.AuctionType;
//import swp.auctionkoi.repository.*;
//import swp.auctionkoi.service.bid.BidService;
//import swp.auctionkoi.service.transaction.TransactionService;
//
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//@Slf4j
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE)
//@Service
//public class BidServiceImpl implements BidService {
//
//    public List<Bid> cachedTop5Bids = new ArrayList<>();
//
//    public List<Bid> getCachedTop5Bids(){
//        return cachedTop5Bids;
//    }
//
//    final AuctionRepository auctionRepository;
//    final AuctionRequestRepository auctionRequestRepository;
//    final BidRepository bidRepository;
//
//    private List<Bid> getListAuctionInProcess(){
//        List<Bid> top5Bids = new ArrayList<>();
//        Auction auction = auctionRepository.getAuctionByStatusInProcess();
//        if(auction != null){
//            AuctionRequest auctionRequest = auctionRequestRepository.findByAuctionIdForLeaderBoard(auction.getId());
//            if(auctionRequest.getRequestStatus().equals(AuctionRequestStatus.APPROVE) && auctionRequest.getMethodType().equals(AuctionType.TRADITIONAL)){
//                top5Bids = bidRepository.findTop5HighestBidsByAuctionId(auction.getId());
//                return top5Bids;
//            }
//        }
//        return null;
//    }
//
//    public void updateTop5TraditionalBids() {
//        List<Bid> top5Bids = getListAuctionInProcess();
//        if (top5Bids != null) {
//            cachedTop5Bids = top5Bids;
//        }
//    }
//}
