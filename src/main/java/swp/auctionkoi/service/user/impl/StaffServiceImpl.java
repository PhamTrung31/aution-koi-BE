package swp.auctionkoi.service.user.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.request.user.UserCreateRequest;
import swp.auctionkoi.dto.request.user.UserUpdateRequest;
import swp.auctionkoi.dto.respone.user.UserResponse;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.mapper.UserMapper;
import swp.auctionkoi.models.*;
import swp.auctionkoi.models.enums.TransactionType;
import swp.auctionkoi.repository.*;
import swp.auctionkoi.service.user.StaffService;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('STAFF')")
public class StaffServiceImpl implements StaffService {

    UserRepository userRepository;

    AuctionRepository auctionRepository;

    PaymentRepository paymentRepository;

    WalletRepository walletRepository;

    TransactionRepository transactionRepository;

    UserMapper userMapper;



    @Override
    public HashMap<Integer, UserResponse> getAllUser() {
        HashMap<Integer, UserResponse> users = new HashMap<>();
        List<User> userList = userRepository.findAll();
        for (User user : userList) {
            users.put(user.getId(), userMapper.toUserResponse(user));
        }
        return users;
    }



    @Override
    public Optional<UserResponse> getUser(int id) {
        User user =  userRepository.findById(id).get();
        return Optional.ofNullable(userMapper.toUserResponse(user));
    }

    @Override
    public Optional<UserResponse> addUser(UserCreateRequest request) {
        User user = new User();

        if(userRepository.existsByUsername(request.getUsername()))
            throw new RuntimeException("Username already exists");

        user = userMapper.toUser(request);

        userRepository.save(user);
        return Optional.ofNullable(userMapper.toUserResponse(user));
    }

    @Override
    public Optional<UserResponse> updateUser(int id, UserUpdateRequest tryUpdateUser) {
        User user = userRepository.findById(id).get();
        if(user != null) {
            userMapper.updateUser(user, tryUpdateUser);
            return Optional.of(userMapper.toUserResponse(userRepository.save(user)));
        }
        return null;
    }

    @Override
    public boolean deleteUser(int id) {
        User user = userRepository.findById(id).get();
        if(user != null) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }


    public String approveWithdrawRequest(int paymentId) throws Exception {
        // Lấy thông tin payment cần phê duyệt
        Optional<Payment> payment = paymentRepository.findById(paymentId);
        if (payment.isEmpty() || payment.get().getPaymentStatus() != 0) {
            throw new AppException(ErrorCode.INVALID_PAYMENT);
        }

        // Lấy thông tin ví liên quan đến payment
        Optional<Wallet> wallet = walletRepository.findByUserId(payment.get().getMember().getId());
        if (wallet.isEmpty()) {
            throw new AppException(ErrorCode.WALLET_NOT_EXISTED);
        }

        // Trừ số tiền từ ví
        if (wallet.get().getBalance() < payment.get().getAmount()) {
            throw new AppException(ErrorCode.NOT_ENOUGH_BALANCE);
        }

        wallet.get().setBalance(wallet.get().getBalance() - payment.get().getAmount());
        walletRepository.save(wallet.get());

        // Cập nhật trạng thái payment là thành công (1)
        payment.get().setPaymentStatus(1); // 1: Success
        paymentRepository.save(payment.get());

        // Tạo transaction để ghi lại quá trình này
        Transaction transaction = new Transaction();
        transaction.setMember(wallet.get().getMember());
        transaction.setWalletId(wallet.get().getId());
        transaction.setPaymentId(payment.get().getId());
        transaction.setTransactionFee(payment.get().getAmount());
        transaction.setTransactionType(TransactionType.WITHDRAW); // Ghi nhận là đã duyệt rút tiền
        transactionRepository.save(transaction);

        return "Withdraw request approved and processed.";
    }

}
