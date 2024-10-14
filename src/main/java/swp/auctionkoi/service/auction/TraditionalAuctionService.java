package swp.auctionkoi.service.auction;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.request.bid.BidRequest;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.*;
import swp.auctionkoi.models.enums.AuctionStatus;
import swp.auctionkoi.models.enums.AuctionType;
import swp.auctionkoi.models.enums.TransactionType;
import swp.auctionkoi.repository.*;

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

    public void placeBid(int auctionId, BidRequest bidRequest) {

        // get auction
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_NOT_FOUND));

        AuctionRequest auctionRequest = auctionRequestRepository.findByAuctionId(auctionId).orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_FOUND));

        //get user
        User user = userRepository.findById(bidRequest.getUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        //get user's wallet
        Wallet wallet = walletRepository.findByUserId(user.getId()).orElseThrow(() -> new AppException(ErrorCode.WALLET_IS_NOT_EXIST));

        //check type method
        if (auctionRequest.getMethodType() != AuctionType.TRADITIONAL.ordinal()) {
            throw new AppException(ErrorCode.INVALID_AUCTION_TYPE);
        }

        // check auction status
        if (auction.getStatus() != AuctionStatus.IN_PROGRESS.ordinal()) {
            throw new AppException(ErrorCode.AUCTION_NOT_STARTED);
        }

        // check value
        if (bidRequest.getBidAmount() <= 0) {
            throw new AppException(ErrorCode.INVALID_BID_AMOUNT);
        }



        //get bidAmount
        float bidAmount = calBidAmount(user, auction.getCurrentPrice(), bidRequest);

        if(bidAmount < auction.getCurrentPrice()){
            throw new AppException(ErrorCode.LOWER_CURRENT_PRICE);
        }

        //not enough money
        if(bidAmount > wallet.getBalance()) {
            throw new AppException(ErrorCode.MONEY_IN_WALLET_NOT_ENOUGH);
        }

        //check bid amount out of max auto amount
        if(bidRequest.isAutoBid()) {
            if (bidAmount > bidRequest.getMaxBidAmount()) {
                throw new AppException(ErrorCode.AUCTION_AUTO_BID_EXCEEDS_MAX);
            }
        }

        Bid bid = buildBid(auction, user, bidRequest, bidAmount);

        wallet.setBalance(wallet.getBalance() - bidAmount);

        Transaction transaction = Transaction.builder()
                .auction(auction)
                .member(user)
                .transactionFee(0)
                .walletId(wallet.getId())
                .transactionType(TransactionType.BID.ordinal())
                .build();

        auction.setCurrentPrice(bidAmount);
        auction.setWinner(user);

        walletRepository.save(wallet);
        transactionRepository.save(transaction);
        auctionRepository.save(auction);
        bidRepository.save(bid);
    }

    /**
     *  For build a new bid
     * */
    private Bid buildBid(Auction auction, User user, BidRequest bidRequest, float bidAmount) {
        return Bid.builder()
                .auction(auction)
                .user(user)
                .isAutoBid(bidRequest.isAutoBid())
                .autoBidMax(bidRequest.getMaxBidAmount())
                .incrementAutobid(bidRequest.getIncrementAutobid().ordinal())
                .bidPrice(bidAmount)
                .build();
    }

    private float calBidAmount(User user, float currentPrice, BidRequest bidRequest) {
        if(bidRequest.isAutoBid()){
            float maxBid = bidRequest.getMaxBidAmount();
            float autoBidAmount = currentPrice;

            autoBidAmount += currentPrice * bidRequest.getIncrementAutobid().ordinal();

            return autoBidAmount;
        } else {

            float bidAmount = bidRequest.getBidAmount();

            if(bidRequest.getIncrementAutobid() != null){
                int fastIncre = bidRequest.getIncrementAutobid().ordinal();

                bidAmount += currentPrice * fastIncre;
            }

            return bidAmount;
        }
    }
}
