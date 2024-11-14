package swp.auctionkoi.service.auction;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.request.bid.BidRequestTraditional;
import swp.auctionkoi.dto.respone.auction.PlaceBidTraditionalInfo;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.*;
import swp.auctionkoi.models.enums.AuctionStatus;
import swp.auctionkoi.models.enums.AuctionType;
import swp.auctionkoi.models.enums.TransactionType;
import swp.auctionkoi.repository.*;
import swp.auctionkoi.service.AuctionNotificationService;
import swp.auctionkoi.service.bid.impl.BidServiceImpl;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('MEMBER')")
@Transactional
public class TraditionalAuctionService {

    AuctionRepository auctionRepository;

    BidRepository bidRepository;

    UserRepository userRepository;

    WalletRepository walletRepository;

    TransactionRepository transactionRepository;

    AuctionRequestRepository auctionRequestRepository;

    AuctionParticipantsRepository auctionParticipantsRepository;

    AuctionNotificationService auctionNotificationService;

    public void placeBid(int auctionId, BidRequestTraditional bidRequestTraditional) {

        // get auction
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_NOT_FOUND));

        if(auction.getExtensionSeconds() <= 0){
            throw new AppException(ErrorCode.CANT_NOT_BID);
        }

        AuctionRequest auctionRequest = auctionRequestRepository.findByAuctionId(auctionId).orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_FOUND));

        //get user
        User user = userRepository.findById(bidRequestTraditional.getUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        AuctionParticipants auctionParticipant = auctionParticipantsRepository.findByAuctionIdAndUserId(auctionId, user.getId());

        //get user's wallet
        Wallet walletUser = walletRepository.findByUserId(user.getId()).orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_EXISTED));


        float bidAmount = bidRequestTraditional.getBidAmount();

        boolean valid = checkValid(auction, auctionParticipant, auctionRequest, user, walletUser, bidAmount, bidRequestTraditional);

        if (valid) {
            List<Bid> bidOfUser = bidRepository.findListBidByAuctionIdAndUserId(auction.getId(), user.getId());
            float amount_find = 0;
            if (!bidOfUser.isEmpty()) {
                for (Bid bid : bidOfUser) {
                    amount_find += bid.getBidAmount();
                }
            }

            if (bidOfUser.isEmpty() && auction.getHighestPrice() == null) {
                Bid bid = buildBid(auction, user, bidRequestTraditional, bidAmount);
                bidRepository.save(bid);
            }
            if (bidOfUser.isEmpty() && bidAmount > auction.getHighestPrice()) {
                if (!(bidAmount % auctionRequest.getIncrementStep() == 0)) {
                    throw new AppException(ErrorCode.NOT_FOLLOW_INCREMENT_STEP);
                }
                Bid bid = buildBid(auction, user, bidRequestTraditional, bidAmount);
                bidRepository.save(bid);
            } else {

                if (!(bidAmount % auctionRequest.getIncrementStep() == 0)) {
                    throw new AppException(ErrorCode.NOT_FOLLOW_INCREMENT_STEP);
                }

                float difference = bidAmount - amount_find;

                Bid bid = buildBid(auction, user, bidRequestTraditional, difference);
                bidRepository.save(bid);
            }


            if (auctionRequest.getEndTime() != null) {
                Instant instantNow = Instant.now();
                // Add 7 hours to Instant
                Instant currentTime = instantNow.plus(Duration.ofHours(7));

                Duration duration = Duration.between(currentTime, auctionRequest.getEndTime());
                if (!duration.isNegative() && duration.toMillis() < 20000) {
                    auctionRequest.setEndTime(auctionRequest.getEndTime().plusSeconds(auction.getExtensionSeconds()));
                    auction.setExtensionSeconds(auction.getExtensionSeconds() - 10);
                    auctionRepository.save(auction);
                }
// tính duration của khoảng thời gian ban đầu (của start và end time ) với khoảng thời gian còn lại
                if (bidAmount >= auctionRequest.getBuyOut()) {

                    if (duration.toMinutes() > 5) {
                        Duration reducedDuration = duration.multipliedBy(25).dividedBy(100);
                        Instant newEndTime = currentTime.plusMillis(reducedDuration.toMillis());
                        auctionRequest.setEndTime(newEndTime);
                        auctionRequestRepository.save(auctionRequest);
                    }

                }
            }

            Transaction transaction;

            if (amount_find != 0) {
                float difference = bidAmount - amount_find;
                walletUser.setBalance(walletUser.getBalance() - difference);
                transaction = Transaction.builder()
                        .auction(auction)
                        .user(user)
                        .transactionFee(0F)
                        .walletId(walletUser.getId())
                        .transactionType(TransactionType.BID)
                        .amount(difference)
                        .build();
            } else {
                walletUser.setBalance(walletUser.getBalance() - bidAmount);
                transaction = Transaction.builder()
                        .auction(auction)
                        .user(user)
                        .transactionFee(0F)
                        .walletId(walletUser.getId())
                        .transactionType(TransactionType.BID)
                        .amount(bidAmount)
                        .build();
            }

            auction.setHighestPrice(bidAmount);
            auction.setWinner(user);

            walletRepository.save(walletUser);
            transactionRepository.save(transaction);
            auctionRepository.save(auction);

            PlaceBidTraditionalInfo placeBidTraditionalInfo = PlaceBidTraditionalInfo.builder()
                    .winner_Id(auction.getWinner().getId())
                    .end_time(auctionRequest.getEndTime())
                    .highest_price(auction.getHighestPrice())
                    .winner_fullname(auction.getWinner().getFullname())
                    .build();

            //send info winnner
            auctionNotificationService.sendPlaceBidTraditionalNotification(placeBidTraditionalInfo);
        }
    }

    /**
     * For build a new bid
     */
    private Bid buildBid(Auction auction, User user, BidRequestTraditional bidRequestTraditional, float bidAmount) {
        Bid bid = Bid.builder()
                .auction(auction)
                .user(user)
                .isAutoBid(bidRequestTraditional.isAutoBid())
                .autoBidMax(bidRequestTraditional.getMaxBidAmount())
                .bidAmount(bidAmount)
                .build();
        return bid;
    }

    private boolean checkValid(Auction auction, AuctionParticipants auctionParticipant, AuctionRequest auctionRequest, User user, Wallet walletUser, float bidAmount, BidRequestTraditional bidRequestTraditional) {
        if (auctionParticipant == null) {
            throw new AppException(ErrorCode.USER_NOT_IN_AUCTION);
        }

        //check type method
        if (auctionRequest.getMethodType() != AuctionType.TRADITIONAL) {
            throw new AppException(ErrorCode.INVALID_AUCTION_TYPE);
        }

        // check auction status
        if (auction.getStatus() != AuctionStatus.IN_PROGRESS) {
            throw new AppException(ErrorCode.AUCTION_NOT_STARTED);
        }

        // check value
        if (bidRequestTraditional.getBidAmount() <= 0) {
            throw new AppException(ErrorCode.INVALID_BID_AMOUNT);
        }

        //get bidAmount
        if (bidAmount <= auction.getHighestPrice()) {
            throw new AppException(ErrorCode.LOWER_CURRENT_PRICE);
        }

        if (auction.getWinner() != null) {
            if (user.getId().equals(auction.getWinner().getId())) {
                throw new AppException(ErrorCode.AlREADY_WIN);
            }
        }

        //not enough money
        if (bidAmount > walletUser.getBalance()) {
            throw new AppException(ErrorCode.MONEY_IN_WALLET_NOT_ENOUGH);
        }

        //check bid amount out of max auto amount
        if (bidRequestTraditional.isAutoBid()) {
            if (bidAmount > bidRequestTraditional.getMaxBidAmount()) {
                throw new AppException(ErrorCode.AUCTION_AUTO_BID_EXCEEDS_MAX);
            }
        }
        return true;
    }

//    private float calBidAmount(User user, float currentPrice, BidRequestTraditional bidRequestTraditional) {
//        if(bidRequestTraditional.isAutoBid()){
//            float maxBid = bidRequestTraditional.getMaxBidAmount();
//            float autoBidAmount = currentPrice;
//
//            autoBidAmount += currentPrice * bidRequestTraditional.getIncrementAutobid().getIncrement();
//
//            if(autoBidAmount > maxBid){
//                autoBidAmount = maxBid;
//            }
//
//            return autoBidAmount;
//        } else {
//
//            float bidAmount = bidRequestTraditional.getBidAmount();
//
//            if(bidRequestTraditional.getIncrementAutobid() != null){
//                float fastIncre = bidRequestTraditional.getIncrementAutobid().getIncrement();
//
//                bidAmount += currentPrice * fastIncre;
//            }
//
//            return bidAmount;
//        }
//    }
}
