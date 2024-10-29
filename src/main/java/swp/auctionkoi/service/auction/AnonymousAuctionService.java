package swp.auctionkoi.service.auction;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.request.bid.BidRequest;
import swp.auctionkoi.dto.request.bid.BidRequestTraditional;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.*;
import swp.auctionkoi.models.enums.AuctionStatus;
import swp.auctionkoi.models.enums.AuctionType;
import swp.auctionkoi.models.enums.TransactionType;
import swp.auctionkoi.repository.*;
import swp.auctionkoi.service.wallet.WalletService;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnonymousAuctionService {

    AuctionRepository auctionRepository;

    UserRepository userRepository;

    BidRepository bidRepository;

    WalletRepository walletRepository;

    AuctionRequestRepository auctionRequestRepository;

    TransactionRepository transactionRepository;

    WalletService walletService;

    AuctionParticipantsRepository auctionParticipantsRepository;


    public void placeBid(int auctionId, BidRequest bidRequest) {

        //get auction
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_NOT_FOUND));

        //get auction request
        AuctionRequest auctionRequest = auctionRequestRepository.findByAuctionId(auctionId).orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_FOUND));

        //get user
        User user = userRepository.findById(bidRequest.getUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        AuctionParticipants auctionParticipants = auctionParticipantsRepository.findByAuctionIdAndUserId(auctionId, user.getId());

        if(auctionParticipants == null) {
            throw new AppException(ErrorCode.USER_NOT_IN_AUCTION);
        }

        //get user's wallet
        Wallet wallet = walletService.getWalletByUserId(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_EXISTED));

        //check type method
        if (auctionRequest.getMethodType() != AuctionType.ANONYMOUS) {
            throw new AppException(ErrorCode.INVALID_AUCTION_TYPE);
        }

        //check auction status
        if (auction.getStatus() != AuctionStatus.IN_PROGRESS) {
            throw new AppException(ErrorCode.AUCTION_NOT_STARTED);
        }

        //check value bid
        if(bidRequest.getBidAmount() <= 0){
            throw new AppException(ErrorCode.INVALID_BID_AMOUNT);
        }

        //check bid lower than start price
        if(bidRequest.getBidAmount() < auctionRequest.getStartPrice()){
            throw new AppException(ErrorCode.INVALID_BID_AMOUNT);
        }

        //check money in wallet can pay bid or not
        if(bidRequest.getBidAmount() > wallet.getBalance()){
            throw new AppException(ErrorCode.LOWER_CURRENT_PRICE);
        }


        // Kiểm tra xem người dùng đã đặt giá chưa
        if (bidRepository.existsByAuctionAndUser(auction, user)) {
            throw new AppException(ErrorCode.BID_ALREADY_PLACED);
        }

        // create bid
        Bid bid = Bid.builder()
                .auction(auction)
                .user(user)
                .bidAmount(bidRequest.getBidAmount())
                .build();

        //set balance wallet
        wallet.setBalance(wallet.getBalance() - bidRequest.getBidAmount());


        //create transaction
        Transaction transaction = Transaction.builder()
                .auction(auction)
                .user(user)
                .walletId(wallet.getId())
                .transactionType(TransactionType.BID)
                .build();

        bidRepository.save(bid);
        walletRepository.save(wallet);
        transactionRepository.save(transaction);
    }

}
