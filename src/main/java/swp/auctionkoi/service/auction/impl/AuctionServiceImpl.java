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


    @Override
    public AuctionJoinResponse JoinAuction(int userId, int auctionId) {
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
        float depositAmount = (float) (auctionRequest.getBuyOut() * 0.15);
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
                .member(user)
                .transactionType(TransactionType.TRANSFER)
                .walletId(wallet.getId())
                .transactionFee(depositAmount)
                .build();
        transactionRepository.save(transaction);

        Transaction transaction1 = Transaction.builder()
                .auction(auction)
                .member(admin)
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
    public void startAuction(int auctionId){
        // Lấy thông tin auction
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(() -> new AppException(ErrorCode.AUCTION_NOT_EXISTED));
        //Get auction request from auction id to get some info
        AuctionRequest auctionRequest = auctionRequestRepository.findByAuctionId(auctionId).orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_FOUND));
        // Lấy danh sách những người tham gia phiên đấu giá
        List<AuctionParticipants> participants = auctionParticipantsRepository.findListAuctionParticipantsByAuctionId(auctionId);

        if(participants.size() >= 7 && auctionRequest.getStartTime().isAfter(Instant.now())) {
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

        // Lấy danh sách những người tham gia phiên đấu giá
        List<AuctionParticipants> participants = auctionParticipantsRepository.findListAuctionParticipantsByAuctionId(auctionId);

        // Lấy thông tin người chiến thắng trực tiếp từ đấu giá

        User winner = auction.getWinner();

        //Lấy thông tin admin và wallet để hoàn tiền deposit và tiền bid
        User admin = (User) userRepository.findFirstByRole(Role.MANAGER)
                .orElseThrow(() -> new AppException(ErrorCode.ADMIN_NOT_FOUND));

        Wallet adminWallet = walletRepository.findByUserId(admin.getId())
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_EXISTED));



        // Xử lý cho những người thua
        for (AuctionParticipants participant : participants) {
            User participantUser = participant.getUser();
            if (!participantUser.getId().equals(winner.getId())) {
                // Nếu người dùng thua, trả lại tiền đặt cọc (trừ 5% phí)
                Wallet userWallet = walletRepository.findByUserId(participantUser.getId()).orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_EXISTED));

                float depositAmount = auction.getDepositAmount();
                float refundAmount = depositAmount * 0.95f; // Trừ 5% phí

                //Tạo transaction thông báo hoàn tiền deposit
                Transaction transaction = Transaction.builder()
                        .auction(auction)
                        .member(participantUser)
                        .transactionType(TransactionType.TRANSFER)
                        .walletId(userWallet.getId())
                        .transactionFee(refundAmount)
                        .build();
                transactionRepository.save(transaction);

                //hoàn tiền deposit cho người thua
                userWallet.setBalance(userWallet.getBalance() + refundAmount);
                walletRepository.save(userWallet);

                //trừ tiền từ ví admin từ việc hoàn tiền
                adminWallet.setBalance(adminWallet.getBalance() - refundAmount);
                walletRepository.save(adminWallet);

                // Lấy bid cao nhất cuối cùng của người dùng trong phiên đấu giá
                Bid highestUserBid = bidRepository.findTopByAuctionIdAndUserIdOrderByBidAmountDesc(auctionId, participantUser.getId()).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_BID_HIGHEST));
                if (highestUserBid != null) {
                    //Tạo transaction cho member thua thông báo hoàn tiền bid
                    Transaction transaction1 = Transaction.builder()
                            .member(participantUser)
                            .auction(auction)
                            .transactionType(TransactionType.TRANSFER)
                            .walletId(userWallet.getId())
                            .transactionFee(highestUserBid.getBidAmount())
                            .build();
                    transactionRepository.save(transaction1);

                    //hoàn tiền bid cho người thua
                    userWallet.setBalance(userWallet.getBalance() + highestUserBid.getBidAmount());
                    walletRepository.save(userWallet);
                    //trừ tiền từ ví admin từ việc hoàn tiền bid
                    adminWallet.setBalance(adminWallet.getBalance() - highestUserBid.getBidAmount());
                    walletRepository.save(adminWallet);
                }
            }
        }

        // Xử lý cho người thắng
        Wallet winnerWallet = walletRepository.findByUserId(winner.getId()).orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_EXISTED));

        // Trả lại tiền đặt cọc cho người thắng (trừ 5% phí)
        float winnerDepositRefund = auction.getDepositAmount() * 0.95f;

        //Tạo transaction thông báo hoàn tiền deposit
        Transaction transaction = Transaction.builder()
                .member(winner)
                .auction(auction)
                .transactionType(TransactionType.TRANSFER)
                .walletId(winnerWallet.getId())
                .transactionFee(winnerDepositRefund)
                .build();
        transactionRepository.save(transaction);

        //hoàn tiền deposit cho người thắng
        winnerWallet.setBalance(winnerWallet.getBalance() + winnerDepositRefund);
        walletRepository.save(winnerWallet);

        //trừ tiền từ ví admin từ việc hoàn tiền
        adminWallet.setBalance(adminWallet.getBalance() - winnerDepositRefund);
        walletRepository.save(adminWallet);

        // Lưu lại ví của người thắng sau khi trừ tiền bid(đã trừ từ lúc bid cuối cùng trong lúc
        //tham gia đáu giá nên k cộng hay trừ)
        winnerWallet.setBalance(winnerWallet.getBalance());
        walletRepository.save(winnerWallet);

        Optional<Bid> highestUserBid = bidRepository.findTopByAuctionIdAndUserIdOrderByBidAmountDesc(auctionId, winner.getId());
        if (highestUserBid.isPresent()) {
            Bid highestBid = highestUserBid.get();
            double winningBidAmount = highestBid.getBidAmount();


            // Example of calling endAuction with manual addresses
            String fromAddress = "123 Admin Street, Admin City";
            String toAddress = "456 Winner Avenue, Winner Town";

            auction.setStatus(AuctionStatus.DELIVERED);

            // Create a delivery record with manually entered addresses
            Delivery delivery = Delivery.builder()
                    .transaction(transaction)
                    .fromAddress(fromAddress)  // Manual input for fromAddress
                    .toAddress(toAddress)      // Manual input for toAddress
                    .deliveryStatus(DeliveryStatus.PREPARING_SHIPMENT)
                    .build();
            deliveryRepository.save(delivery);
            //set status end auction
            //tao delivery
            //khi delivery status success chuyen tien tu auction current price
        }
    }

    @Override
    public Optional<Auction> getAuction(int id) {
        return null;
    }

    @Override
    public AuctionResponse createAuction (AuctionDTO auctionDTO){
        AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionDTO.getAuctionRequestId())
                .orElseThrow(() -> new IllegalArgumentException("Auction Request not found with id: " + auctionDTO.getAuctionRequestId()));

        if (!auctionRequest.getRequestStatus().equals(KoiStatus.APPROVED)) {
            return AuctionResponse.builder()
                    .status("400")
                    .message("Auction Request is not approved")
                    .build();
        }

//        boolean isTimeConflict = auctionRepository.existsByStartTimeBetweenOrEndTimeBetween(
//                auctionDTO.getStartTime(), auctionDTO.getEndTime(),
//                auctionDTO.getStartTime(), auctionDTO.getEndTime());
//
//        if (isTimeConflict) {
//            return AuctionResponse.builder()
//                    .status("400")
//                    .message("The selected auction time conflicts with another auction")
//                    .build();
//        }]

        Auction auction = new Auction();
        auction.setFish(auctionRequest.getFish());
        auction.setCurrentPrice(auctionRequest.getStartPrice());
        auction.setStatus(AuctionStatus.PENDING);

        auctionRepository.save(auction);

        return AuctionResponse.builder()
                .status("200")
                .message("Auction created successfully")
                .auctionId(auction.getId())
                .build();
    }

    @Override
    public Auction updateAuction(Auction auction) {
        return null;
    }

    @Override
    public void deleteAuction(int id) {

    }

    @Override
    public void viewDetailAuction(int auctionId) {

    }

    @Override
    public HashMap<Integer, Bid> viewHistoryBidAuction(int auctionId) {
        return null;
    }



    @Override
    public void checkUserDepositBeforeBidding(int auctionId, int userId) {

    }

    @Override
    public void handleBidDuringAuction(int auctionId, int userId, float bidAmount) {

    }

    @Override
    public void closeAuction(int auctionId) {

    }
}
