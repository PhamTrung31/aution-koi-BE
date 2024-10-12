package swp.auctionkoi.service.auction.impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.respone.auction.AuctionJoinResponse;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.*;
import swp.auctionkoi.models.enums.Role;
import swp.auctionkoi.repository.*;
import swp.auctionkoi.service.auction.AuctionService;
import swp.auctionkoi.service.wallet.WalletService;
import swp.auctionkoi.service.wallet.impl.WalletServiceImpl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class AuctionServiceImpl implements AuctionService {

    WalletRepository walletRepository;

    AuctionRepository auctionRepository;

    UserRepository userRepository;

    AuctionRequestRepository auctionRequestRepository;

    AuctionParticipantsRepository auctionParticipantsRepository;

    BidRepository bidRepository;

    public AuctionJoinResponse JoinAuction(int userId, int auctionId) {
        // Lấy thông tin user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Chỉ cho phép MEMBER tham gia
        if (user.getRole() != Role.MEMBER) {
            throw new AppException(ErrorCode.ONLY_MEMBER_ALLOWED);
        }

        // Lấy thông tin ví của user
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_EXISTED));

        // Lấy thông tin auction
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_NOT_EXISTED));
        // Kiểm tra xem user đã tham gia auction này chưa
        Optional<AuctionParticipants> existedParticipants =
                Optional.ofNullable(auctionParticipantsRepository.findByAuctionIdAndUserId(auctionId, userId));

        if (existedParticipants.isPresent()) {
            throw new AppException(ErrorCode.USER_EXISTED_AUCTION);
        }

        // Lấy thông tin yêu cầu đấu giá (auction request) để tính toán số tiền đặt cọc
        AuctionRequest auctionRequest = auctionRequestRepository
                .findByAuctionId(auctionId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_NOT_EXISTED));

        // Tính số tiền đặt cọc (15% giá trị buyout)
        Float depositAmount = (float) (auctionRequest.getBuyOut() * 0.15);

        // Kiểm tra số dư trong ví của user có đủ để đặt cọc không
        if (wallet.getBalance() < depositAmount) {
            throw new AppException(ErrorCode.NOT_ENOUGH_BALANCE);
        }

        // Trừ tiền đặt cọc từ ví người dùng
        wallet.setBalance(wallet.getBalance() - depositAmount);
        walletRepository.save(wallet);

        // Lưu thông tin số tiền đặt cọc vào phiên đấu giá
        auction.setDepositAmount(depositAmount);
        auctionRepository.save(auction);

        // user join vào danh sách người tham gia đấu giá
        AuctionParticipants newParticipant = new AuctionParticipants();
        newParticipant.setUser(user);
        newParticipant.setAuction(auction);
        newParticipant.setJoinDate(Instant.now());
        auctionParticipantsRepository.save(newParticipant);

        AuctionJoinResponse response = new AuctionJoinResponse();
        response.setUserId(user.getId());
        response.setAuctionId(auction.getId());
        response.setJoinDate(newParticipant.getJoinDate());

        return response;
    }

    @Transactional
    @Override
    public void endAuction(int auctionId) {
        // Lấy thông tin auction
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_NOT_EXISTED));

        // Lấy danh sách những người tham gia phiên đấu giá
        List<AuctionParticipants> participants = auctionParticipantsRepository.findByAuctionId(auctionId);

        // Lấy thông tin bid cao nhất
        Optional<Bid> winningBidOpt = bidRepository.findTopByAuctionIdOrderByBidAmountDesc(auctionId);
        Bid winningBid = winningBidOpt.orElseThrow(() -> new RuntimeException("No bids found for the auction"));

        // Lấy thông tin người thắng (người đặt bid cao nhất)
        User winner = winningBid.getUser();

        // Xử lý cho những người thua
        for (AuctionParticipants participant : participants) {
            User participantUser = participant.getUser();
            if (!participantUser.getId().equals(winner.getId())) {
                // Nếu người dùng thua, trả lại tiền đặt cọc (trừ 5% phí)
                Wallet userWallet = walletRepository.findByUserId(participantUser.getId())
                        .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_EXISTED));

                float depositAmount = auction.getDepositAmount();
                float refundAmount = depositAmount * 0.95f; // Trừ 5% phí

                userWallet.setBalance(userWallet.getBalance() + refundAmount);
                walletRepository.save(userWallet);

                // Nếu người dùng đã đặt bid, trả lại tiền bid
                List<Bid> userBids = bidRepository.findByAuctionIdAndUserId(auctionId, participantUser.getId());
                for (Bid userBid : userBids) {
                    userWallet.setBalance(userWallet.getBalance() + userBid.getBidAmount());
                }
                walletRepository.save(userWallet);
            }
        }

        // Xử lý cho người thắng
        Wallet winnerWallet = walletRepository.findByUserId(winner.getId())
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_EXISTED));

        // Trả lại tiền đặt cọc cho người thắng (trừ 5% phí)
        float winnerDepositRefund = auction.getDepositAmount() * 0.95f;
        winnerWallet.setBalance(winnerWallet.getBalance() + winnerDepositRefund);
        walletRepository.save(winnerWallet);

        // Lấy breeder của phiên đấu giá và ví của breeder
        AuctionRequest auctionRequest = auctionRequestRepository.findByAuctionId(auctionId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_NOT_EXISTED));

        User breeder = auctionRequest.getBreeder();
        Wallet breederWallet = walletRepository.findByUserId(breeder.getId())
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_EXISTED));

        // Trả lại tiền bid của người thắng những lần trước khi bid giá cuối
        List<Bid> winnerBids = bidRepository.findByAuctionIdAndUserId(auctionId, winner.getId());

        // Nếu người thắng chỉ có duy nhất 1 lần bid
        if (winnerBids.size() == 1) {
            // Không cần trừ tiền bid từ ví của người thắng vì đã trừ từ trước khi bid
            System.out.println("Winner only bid once, no need to deduct bid amount.");
        } else {
            // Nếu người thắng có nhiều lần bid
            for (Bid bid : winnerBids) {
                if (!bid.getId().equals(winningBid.getId())) {
                    // Hoàn tiền cho các bid trước đó
                    winnerWallet.setBalance(winnerWallet.getBalance() + bid.getBidAmount());
                }
            }
        }
        // Lưu lại ví của người thắng sau khi hoàn tiền cho các bid trước
        walletRepository.save(winnerWallet);


        // Chuyển tiền bid từ ví của người thắng sang ví breeder (trừ 10% phí)
        double amountForBreeder = winningBid.getBidAmount() * 0.90; // Trừ 10% phí
        breederWallet.setBalance(breederWallet.getBalance() + amountForBreeder);
        walletRepository.save(breederWallet);
    }
}


















