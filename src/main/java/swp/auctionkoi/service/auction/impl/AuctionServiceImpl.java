package swp.auctionkoi.service.auction.impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.request.AuctionDTO;
import swp.auctionkoi.dto.respone.AuctionResponse;
import swp.auctionkoi.dto.respone.auction.AuctionJoinResponse;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.*;
import swp.auctionkoi.models.enums.*;
import swp.auctionkoi.repository.*;
import swp.auctionkoi.service.auction.AuctionService;
import swp.auctionkoi.service.wallet.WalletService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    TransactionRepository transactionRepository;

    DeliveryRepository deliveryRepository;

    WalletService walletService;

    KoiFishRepository koiFishRepository;

    private static final float DEPOSIT_RATE = 0.15f;

    @Override
    public AuctionJoinResponse joinAuction(int userId, int auctionId) {
        // Lấy thông tin user
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Chỉ cho phép MEMBER tham gia
        if (user.getRole() != Role.MEMBER) {
            throw new AppException(ErrorCode.ONLY_MEMBER_ALLOWED);
        }

        // Lấy thông tin ví của user
        Wallet wallet = walletRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_EXISTED));


        // Fetch admin by known admin ID or role
        User admin = (User) userRepository.findFirstByRole(Role.MANAGER)
                .orElseThrow(() -> new AppException(ErrorCode.ADMIN_NOT_FOUND));

        Wallet adminWallet = walletRepository.findByUserId(admin.getId())
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_EXISTED));


        // Lấy thông tin auction
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(() -> new AppException(ErrorCode.AUCTION_NOT_EXISTED));
        // Kiểm tra xem user đã tham gia auction này chưa
        Optional<AuctionParticipants> existedParticipants = Optional.ofNullable(auctionParticipantsRepository.findByAuctionIdAndUserId(auctionId, userId));

        if (existedParticipants.isPresent()) {
            throw new AppException(ErrorCode.USER_EXISTED_AUCTION);
        }

        // Lấy thông tin yêu cầu đấu giá (auction request) để tính toán số tiền đặt cọc
        AuctionRequest auctionRequest = auctionRequestRepository.findByAuctionId(auctionId).orElseThrow(() -> new AppException(ErrorCode.AUCTION_NOT_EXISTED));

        // Thời gian chốt người dùng tham gia đấu giá trước 10 PHÚT
        Instant now = Instant.now();
        Instant starttime = auctionRequest.getStartTime().minus(10, ChronoUnit.MINUTES);

        if (now.isAfter(starttime)) {
            throw new AppException(ErrorCode.AUCTION_JOIN_CLOSED);
        }

        // Tính số tiền đặt cọc (15% giá trị buyout)
        float depositAmount = (float) (auctionRequest.getBuyOut() * DEPOSIT_RATE);
        //invalid depositAmount


        // Kiểm tra số dư trong ví của user có đủ để đặt cọc không
        if (wallet.getBalance() < depositAmount) {
            throw new AppException(ErrorCode.NOT_ENOUGH_BALANCE);
        }

        // Lưu thông tin số tiền đặt cọc vào phiên đấu giá
        auction.setDepositAmount(depositAmount);
        auctionRepository.save(auction);

        // Trừ tiền đặt cọc từ ví người dùng
        wallet.setBalance(wallet.getBalance() - depositAmount);
        walletRepository.save(wallet);

        // Cộng tiền vào ví hệ thống(manager)
        adminWallet.setBalance(adminWallet.getBalance() + depositAmount);
        walletRepository.save(adminWallet);

        //Tạo transaction thông báo trù tiền deposit
        Transaction transaction = Transaction.builder()
                .auction(auction)
                .user(user)
                .transactionType(TransactionType.TRANSFER)
                .walletId(wallet.getId())
                .transactionFee(depositAmount)
                .build();
        transactionRepository.save(transaction);

        Transaction transaction1 = Transaction.builder()
                .auction(auction)
                .user(admin)
                .transactionType(TransactionType.TRANSFER)
                .walletId(wallet.getId())
                .transactionFee(depositAmount)
                .build();
        transactionRepository.save(transaction1);


        // user join vào danh sách người tham gia đấu giá
        AuctionParticipants newParticipant = AuctionParticipants.builder()
                .user(user)
                .auction(auction).
                build();
        auctionParticipantsRepository.save(newParticipant);

        AuctionJoinResponse response = AuctionJoinResponse.builder()
                .userId(user.getId())
                .auctionId(auction.getId())
                .build();

        return response;
    }

    @Transactional
    @Override
    public void startAuction(int auctionId) {
        // Lấy thông tin auction
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(() -> new AppException(ErrorCode.AUCTION_NOT_EXISTED));
        //Get auction request from auction id to get some info
        AuctionRequest auctionRequest = auctionRequestRepository.findByAuctionId(auctionId).orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_FOUND));
        // Lấy danh sách những người tham gia phiên đấu giá
        List<AuctionParticipants> participants = auctionParticipantsRepository.findListAuctionParticipantsByAuctionId(auctionId);
        //bidder in auction must bigger than 1 (real run will be 7). Check the start time is after now or not
        if (participants.size() > 1 && auctionRequest.getStartTime().isAfter(Instant.now())) {
            auction.setStatus(AuctionStatus.IN_PROGRESS);
            auction.setWinner(null);
            auction.setHighestPrice(0.0F);
            auctionRepository.save(auction);
        }
    }

    private List<AuctionRequest> getSortedAuctionRequestsWithAuctionsByStartTime() {
        List<AuctionRequest> auctionRequests = auctionRequestRepository.findByAuctionIsNotNull();
        return auctionRequests.stream()
                .filter(ar -> ar.getStartTime() != null)  //start item in list have startTime is null
                .sorted(Comparator.comparing(AuctionRequest::getStartTime))  //sort by startTime
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void endAuction(int auctionId) {
        // Lấy thông tin auction
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(() -> new AppException(ErrorCode.AUCTION_NOT_EXISTED));

        //get list participant in this auction
        List<AuctionParticipants> participants = auctionParticipantsRepository.findListAuctionParticipantsByAuctionId(auction.getId());


        //get admin
        User admin = userRepository.findByUsername("admin")
                .orElseThrow(() -> new AppException(ErrorCode.ADMIN_NOT_FOUND));

        backDepositAmount(auction, participants,admin);
        backMoneyBid(auction, participants,admin);
        createDeliveryKoiForWinner(auction);
        auction.setStatus(AuctionStatus.COMPLETED); //staff will update after finish delivery
        auctionRepository.save(auction);
    }

    private void backDepositAmount(Auction auction, List<AuctionParticipants> participants, User admin) {

        //get deposit
        float depositAmount = auction.getDepositAmount();
        float refundAmount = depositAmount * 0.95f; //minus 5% fee for system

        //refund
        for (AuctionParticipants participant : participants) {
            User participantUser = participant.getUser();
            //get user wallet
            Wallet userWallet = walletRepository.findByUserId(participantUser.getId()).orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_EXISTED));
            //get system wallet from admin object to refund deposit amount
            Wallet adminWallet = walletRepository.findByUserId(admin.getId()).orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_EXISTED));
            //add money to user wallet
            userWallet.setBalance(userWallet.getBalance() + refundAmount);
            //save
            walletRepository.save(userWallet);
            //minus money in system wallet
            adminWallet.setBalance(adminWallet.getBalance() - refundAmount);
            //save
            walletRepository.save(adminWallet);
            //create transaction
            Transaction transaction = Transaction.builder()
                    .auction(auction)
                    .user(participant.getUser())
                    .transactionType(TransactionType.TRANSFER)
                    .walletId(userWallet.getId())
                    .transactionFee(0)
                    .amount(refundAmount)
			        .build();
            transactionRepository.save(transaction);
        }
    }

    private void backMoneyBid(Auction auction, List<AuctionParticipants> participants,  User admin) {
        for(AuctionParticipants participant : participants) {
            Bid highestUserBid = bidRepository.findTopByAuctionIdAndUserIdOrderByBidAmountDesc(auction.getId(), participant.getId());
            Wallet userWallet = walletRepository.findByUserId(participant.getId()).orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_EXISTED));
            Wallet adminWallet = walletRepository.findByUserId(admin.getId()).orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_EXISTED));
            if (highestUserBid != null) {
                //transaction backmoney when user have bid
                Transaction transaction = Transaction.builder()
                        .user(participant.getUser())
                        .auction(auction)
                        .transactionType(TransactionType.TRANSFER)
                        .walletId(userWallet.getId())
                        .transactionFee(0)
                        .amount(highestUserBid.getBidAmount())
                        .build();
                transactionRepository.save(transaction);

                //back money
                userWallet.setBalance(userWallet.getBalance() + highestUserBid.getBidAmount());
                walletRepository.save(userWallet);
                //minus money from system wallet
                adminWallet.setBalance(adminWallet.getBalance() - highestUserBid.getBidAmount());
                walletRepository.save(adminWallet);
            }
        }
    }

    private void createDeliveryKoiForWinner(Auction auction) {
        User winner = auction.getWinner();
        if(winner != null) {
            // Example of calling endAuction with manual addresses
            String fromAddress = "123 Admin Street, Admin City";
            String toAddress = "456 Winner Avenue, Winner Town";

            Transaction transaction = transactionRepository.findTopByAuctionIdAndUserIdOrderByAmountDesc(auction.getId(), winner.getId());

            // Create a delivery record with manually entered addresses
            Delivery delivery = Delivery.builder()
                    .transaction(transaction)
                    .fromAddress(fromAddress)  // Manual input for fromAddress
                    .toAddress(toAddress)      // Manual input for toAddress
                    .deliveryStatus(DeliveryStatus.PREPARING_SHIPMENT)
                    .build();
            deliveryRepository.save(delivery);
        }
    }
}
