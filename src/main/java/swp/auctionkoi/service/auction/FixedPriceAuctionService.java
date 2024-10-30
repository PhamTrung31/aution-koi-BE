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

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FixedPriceAuctionService {

    AuctionRepository auctionRepository;
    AuctionRequestRepository auctionRequestRepository;
    UserRepository userRepository;
    WalletRepository walletRepository;
    TransactionRepository transactionRepository;
    BidRepository bidRepository;
    AuctionParticipantsRepository auctionParticipantsRepository;
    
    public void placeBid(int auctionId, BidRequest bidRequest) {

        //get data from database
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(() -> new AppException(ErrorCode.AUCTION_NOT_FOUND));

        //check auction status
        if (auction.getStatus() != AuctionStatus.IN_PROGRESS) {
            throw new AppException(ErrorCode.AUCTION_NOT_STARTED);
        }

        AuctionRequest auctionRequest = auctionRequestRepository.findByAuctionId(auctionId).orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_FOUND));

        //check type method
        if (auctionRequest.getMethodType() != AuctionType.FIXED_PRICE) {
            throw new AppException(ErrorCode.INVALID_AUCTION_TYPE);
        }

        User user = userRepository.findById(bidRequest.getUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        AuctionParticipants auctionParticipant = auctionParticipantsRepository.findByAuctionIdAndUserId(auctionId, user.getId());

        if (auctionParticipant == null) {
            throw new AppException(ErrorCode.USER_NOT_IN_AUCTION);
        }

        Wallet walletUser = walletRepository.findByUserId(user.getId()).orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_EXISTED));

        //check value bid
        if (bidRequest.getBidAmount() <= 0) {
            throw new AppException(ErrorCode.INVALID_BID_AMOUNT);
        }

        //check bid lower than start price
        if (bidRequest.getBidAmount() < auctionRequest.getBuyOut() || bidRequest.getBidAmount() > auctionRequest.getBuyOut()) {
            throw new AppException(ErrorCode.INVALID_BID_AMOUNT);
        }

        //check money in wallet can pay bid or not
        if (bidRequest.getBidAmount() > walletUser.getBalance()) {
            throw new AppException(ErrorCode.LOWER_CURRENT_PRICE);
        }

        // Kiểm tra xem người dùng đã đặt giá chưa
        if (bidRepository.existsByAuctionAndUser(auction, user)) {
            throw new AppException(ErrorCode.BID_ALREADY_PLACED);
        }

        // save bid
        Bid bid = Bid.builder()
                .auction(auction)
                .user(user)
                .bidAmount(bidRequest.getBidAmount())
                .build();

        walletUser.setBalance(walletUser.getBalance() - bidRequest.getBidAmount());

        Transaction transaction = Transaction.builder()
                .auction(auction)
                .user(user)
                .walletId(walletUser.getId())
                .transactionType(TransactionType.BID)
                .build();

        walletRepository.save(walletUser);
        transactionRepository.save(transaction);
        bidRepository.save(bid);
    }
}
