package swp.auctionkoi.service.auction;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.request.bid.BidRequestTraditional;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.*;
import swp.auctionkoi.models.enums.AuctionStatus;
import swp.auctionkoi.models.enums.AuctionType;
import swp.auctionkoi.models.enums.TransactionType;
import swp.auctionkoi.repository.*;
import swp.auctionkoi.service.bid.impl.BidServiceImpl;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('MEMBER')")
public class TraditionalAuctionService {

    AuctionRepository auctionRepository;

    BidRepository bidRepository;

    UserRepository userRepository;

    WalletRepository walletRepository;

    TransactionRepository transactionRepository;

    AuctionRequestRepository auctionRequestRepository;

    AuctionParticipantsRepository auctionParticipantsRepository;


    public void placeBid(int auctionId, BidRequestTraditional bidRequestTraditional) {

        // get auction
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_NOT_FOUND));

        AuctionRequest auctionRequest = auctionRequestRepository.findByAuctionId(auctionId).orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_FOUND));

        //get user
        User user = userRepository.findById(bidRequestTraditional.getUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        AuctionParticipants auctionParticipant = auctionParticipantsRepository.findByAuctionIdAndUserId(auctionId, user.getId());

        if(auctionParticipant == null) {
            throw new AppException(ErrorCode.USER_NOT_IN_AUCTION);
        }

        //get user's wallet
        Wallet walletUser = walletRepository.findByUserId(user.getId()).orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_EXISTED));

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
        float bidAmount = bidRequestTraditional.getBidAmount();

        if(bidAmount <= auction.getHighestPrice()){
            throw new AppException(ErrorCode.LOWER_CURRENT_PRICE);
        }

        //not enough money
        if(bidAmount > walletUser.getBalance()) {
            throw new AppException(ErrorCode.MONEY_IN_WALLET_NOT_ENOUGH);
        }

        //check bid amount out of max auto amount
        if(bidRequestTraditional.isAutoBid()) {
            if (bidAmount > bidRequestTraditional.getMaxBidAmount()) {
                throw new AppException(ErrorCode.AUCTION_AUTO_BID_EXCEEDS_MAX);
            }
        }


        Bid findBidByUser = bidRepository.findByAuctionIdAndUserId(auction.getId(), user.getId());

        if(findBidByUser == null) {
            Bid bid = buildBid(auction, user, bidRequestTraditional, bidAmount);
            bidRepository.save(bid);
        } else {
            findBidByUser.setBidAmount(bidAmount);
            bidRepository.save(findBidByUser);
        }

        //for check bid place close the end time or not
        Bid checkBid = bidRepository.findByAuctionIdAndUserId(auction.getId(), user.getId());
        Duration duration = Duration.between(checkBid.getBidUpdatedDate(), auctionRequest.getEndTime());
        if (!duration.isNegative() && duration.toMillis() < 10000) {
            auctionRequest.setEndTime(auctionRequest.getEndTime().plusSeconds(auction.getExtensionSeconds()));
            auction.setExtensionSeconds(auction.getExtensionSeconds() - 10);
            auctionRepository.save(auction);
        }

        walletUser.setBalance(walletUser.getBalance() - bidAmount);

        Transaction transaction = Transaction.builder()
                .auction(auction)
                .user(user)
                .transactionFee(0F)
                .walletId(walletUser.getId())
                .transactionType(TransactionType.BID)
                .amount(bidAmount)
                .build();

        auction.setHighestPrice(bidAmount);
        auction.setWinner(user);

        walletRepository.save(walletUser);
        transactionRepository.save(transaction);
        auctionRepository.save(auction);

    }

    /**
     *  For build a new bid
     * */
    private Bid buildBid(Auction auction, User user, BidRequestTraditional bidRequestTraditional, float bidAmount) {
        return Bid.builder()
                .auction(auction)
                .user(user)
                .isAutoBid(bidRequestTraditional.isAutoBid())
                .autoBidMax(bidRequestTraditional.getMaxBidAmount())
                .bidAmount(bidAmount)
                .build();
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
