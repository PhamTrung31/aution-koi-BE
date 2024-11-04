package swp.auctionkoi.service.auction.impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import swp.auctionkoi.dto.respone.auction.AuctionJoinResponse;
import swp.auctionkoi.dto.respone.auction.AuctionHistoryResponse;
import swp.auctionkoi.dto.respone.auction.UserWinAucionInfo;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.*;
import swp.auctionkoi.models.enums.*;
import swp.auctionkoi.repository.*;
import swp.auctionkoi.service.AuctionNotificationService;
import swp.auctionkoi.service.auction.AuctionService;
import swp.auctionkoi.service.wallet.WalletService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin("*")
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

    AuctionNotificationService auctionNotificationService;

    private static final float DEPOSIT_RATE = 0.15f;

    @PreAuthorize("hasRole('MEMBER')")
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
                .amount(depositAmount)
                .transactionFee(0F)
                .build();
        transactionRepository.save(transaction);

        Transaction transaction1 = Transaction.builder()
                .auction(auction)
                .user(admin)
                .transactionType(TransactionType.TRANSFER)
                .walletId(wallet.getId())
                .amount(depositAmount)
                .transactionFee(0F)
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

    @Override
    // Hàm kiểm tra tham gia đấu giá
    public String checkUserParticipationInAuction(int userId, int auctionId) {
        Optional<AuctionParticipants> existedParticipants = Optional.ofNullable(
                auctionParticipantsRepository.findByAuctionIdAndUserId(auctionId, userId)
        );

        return existedParticipants.isPresent() ? "Joined auction" : "Haven't join auction";
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

        auction.setStatus(AuctionStatus.IN_PROGRESS);
        auction.setWinner(null);
        auction.setHighestPrice(0.0F);
        auctionRepository.save(auction);
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
        AuctionRequest auctionRequest = auctionRequestRepository.findByAuctionId(auctionId).orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_EXISTED));
        //get admin
        User admin = userRepository.findByUsername("admin").orElseThrow(() -> new AppException(ErrorCode.ADMIN_NOT_FOUND));

        List<AuctionParticipants> participants = auctionParticipantsRepository.findListAuctionParticipantsByAuctionId(auctionRequest.getAuction().getId());


        if (auctionRequest.getMethodType().equals(AuctionType.TRADITIONAL)) {
            backDepositAmount(auctionRequest.getAuction(), participants, admin);
            List<Bid> listBid = bidRepository.getListBidByAuctionId(auctionRequest.getAuction().getId());
            if(auctionRequest.getAuction().getWinner() == null && listBid.isEmpty()){
                auctionRequest.getAuction().setStatus(AuctionStatus.UNSOLD);
                return;
            }
            createDeliveryKoiForWinner(auctionRequest.getAuction());
        }

        if (auctionRequest.getMethodType().equals(AuctionType.FIXED_PRICE)) {
            backDepositAmount(auctionRequest.getAuction(), participants, admin);
            List<Bid> listBid = bidRepository.getListBidByAuctionId(auctionRequest.getAuction().getId());
            if(listBid.size() > 1) {
                User winner = randomWinner(listBid);
                auctionRequest.getAuction().setWinner(winner);
                createDeliveryKoiForWinner(auctionRequest.getAuction());
            } else if (listBid.size() == 1){
                User winner = listBid.get(0).getUser();
                auctionRequest.getAuction().setWinner(winner);
                createDeliveryKoiForWinner(auctionRequest.getAuction());
            } else{
                auctionRequest.getAuction().setWinner(null);
                auctionRequest.getAuction().setStatus(AuctionStatus.UNSOLD);
                return;
            }
            UserWinAucionInfo userWinAucionInfo = UserWinAucionInfo.builder()
                    .user_id(auctionRequest.getAuction().getWinner().getId())
                    .full_name(auctionRequest.getAuction().getWinner().getFullname())
                    .avatar_url(auctionRequest.getAuction().getWinner().getAvatarUrl())
                    .highest_bid(auctionRequest.getAuction().getHighestPrice())
                    .build();
            auctionNotificationService.sendWinnerOfFixedPrice(userWinAucionInfo);
        }

        if (auctionRequest.getMethodType().equals(AuctionType.ANONYMOUS)) {
            backDepositAmount(auctionRequest.getAuction(), participants, admin);
            Bid bid = bidRepository.getBidHighestAmountAndEarliestInAuction(auctionRequest.getAuction().getId());
            if(bid != null) {
                User winner = bid.getUser();
                auctionRequest.getAuction().setWinner(winner);
                createDeliveryKoiForWinner(auctionRequest.getAuction());
            } else {
                auctionRequest.getAuction().setStatus(AuctionStatus.UNSOLD);
                return;
            }
        }

        backMoneyBid(auctionRequest.getAuction(), participants, admin);
        auctionRequest.getAuction().setStatus(AuctionStatus.COMPLETED); //staff will update after finish delivery
        auctionRequestRepository.save(auctionRequest);
    }

    @Override
    public Auction getAuctionById(int auctionId) {
        return auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_NOT_EXISTED));
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
                    .transactionFee(0F)
                    .amount(refundAmount)
                    .build();
            transactionRepository.save(transaction);
        }
    }

    private void backMoneyBid(Auction auction, List<AuctionParticipants> participants, User admin) {
        for (AuctionParticipants participant : participants) {
            if(!participant.getUser().getId().equals(auction.getWinner().getId())){
                Bid bid = bidRepository.findByAuctionIdAndUserId(auction.getId(), participant.getId());
                if (bid != null) {
                    Wallet userWallet = walletRepository.findByUserId(participant.getId()).orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_EXISTED));
                    Wallet adminWallet = walletRepository.findByUserId(admin.getId()).orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_EXISTED));
                    //transaction backmoney when user have bid
                    Transaction transaction = Transaction.builder()
                            .user(participant.getUser())
                            .auction(auction)
                            .transactionType(TransactionType.TRANSFER)
                            .walletId(userWallet.getId())
                            .transactionFee(0F)
                            .amount(bid.getBidAmount())
                            .build();
                    transactionRepository.save(transaction);

                    //back money
                    userWallet.setBalance(userWallet.getBalance() + bid.getBidAmount());
                    walletRepository.save(userWallet);
                    //minus money from system wallet
                    adminWallet.setBalance(adminWallet.getBalance() - bid.getBidAmount());
                    walletRepository.save(adminWallet);
                }
            }
        }
    }

    private void createDeliveryKoiForWinner(Auction auction) {
        User winner = auction.getWinner();
        AuctionRequest auctionRequest = auctionRequestRepository.findByAuctionId(auction.getId()).get();
        if (winner != null) {
            //from address
            User breeder = auctionRequest.getUser();
            String fromAddress = breeder.getAddress();
            String toAddress = winner.getAddress();

            // Create a delivery record with manually entered addresses
            Delivery delivery = Delivery.builder()
                    .fromAddress(fromAddress)  // Manual input for fromAddress
                    .toAddress(toAddress)      // Manual input for toAddress
                    .deliveryStatus(DeliveryStatus.PREPARING_SHIPMENT)
                    .build();
            deliveryRepository.save(delivery);
        }
    }

    private User randomWinner(List<Bid> listBid) {
        int randomIndex = ThreadLocalRandom.current().nextInt(listBid.size()); // select index from 0 to size - 1
        return listBid.get(randomIndex).getUser(); // return user winner
    }



    public List<AuctionHistoryResponse> getListAuctionComplete(){
        List<Auction> auctions = auctionRepository.getListAuctionCompleteByStatus(4);
        List<AuctionHistoryResponse> resonpses = new ArrayList<>();
        for(Auction auction : auctions){
            resonpses.add(AuctionHistoryResponse.builder()
                            .auction_id(auction.getId())
                            .fish_id(auction.getFish().getId())
                            .build());
        }
        return resonpses;
    }
}
