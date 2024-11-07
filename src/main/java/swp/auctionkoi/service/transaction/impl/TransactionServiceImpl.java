package swp.auctionkoi.service.transaction.impl;//package swp.auctionkoi.service.transaction.impl;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;
//import swp.auctionkoi.models.Transaction;
//import swp.auctionkoi.models.Wallet;
//import swp.auctionkoi.models.enums.TransactionType;
//import swp.auctionkoi.repository.TransactionRepository;
//import swp.auctionkoi.repository.UserRepository;
//import swp.auctionkoi.repository.WalletRepository;
//import swp.auctionkoi.service.transaction.TransactionService;
//
//import java.time.Instant;
//import java.util.List;
//
//@Service
//public class TransactionServiceImpl implements TransactionService {
//
//
//    @Autowired
//    private TransactionRepository transactionRepository;
//
//    @Autowired
//    private WalletRepository walletRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Override
//    public void saveTransaction(Integer userId, Double amount) {
//        // Tìm thông tin ví của người dùng
//        Wallet wallet = walletRepository.findByUserId(userId)
//                .orElseThrow(() -> new RuntimeException("Wallet not found"));
//        if (wallet != null) {
//            // Tạo transaction
//            Transaction transaction = Transaction.builder()
//                    .member(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Người dùng không tồn tại")))
//                    .transactionDate(Instant.now())
//                    .transactionType(TransactionType.TOP_UP)  // Kiểu giao dịch là nạp tiền
//                    .transactionFee(0.0F)  // Miễn phí giao dịch cho nạp tiền
//                    .walletId(wallet.getId())  // Liên kết giao dịch với ví người dùng
//                    .build();
//            transactionRepository.save(transaction);
//        } else {
//            throw new RuntimeException("Không tìm thấy ví của người dùng này.");
//        }
//    }



import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.Transaction;
import swp.auctionkoi.models.User;
import swp.auctionkoi.repository.TransactionRepository;
import swp.auctionkoi.service.transaction.TransactionService;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('STAFF')")
public class TransactionServiceImpl implements TransactionService {


    TransactionRepository transactionRepository;


    public HashMap<Integer, Transaction> getAllTransaction(){
        HashMap<Integer, Transaction> transactions = new HashMap<>();
        List<Transaction> transactionList = transactionRepository.findAll();
        for (Transaction transaction : transactionList) {
            transactions.put(transaction.getId(), transaction);
        }
        return transactions;
    }

    public Optional<Transaction> getTransaction(int transactionId){
        if(transactionId <= 0){
            throw new AppException(ErrorCode.INVALID_TRANSACTION_ID);
        }
        Transaction transaction = transactionRepository.findById(transactionId).get();
        return Optional.ofNullable(transaction);
    }

    // Hàm chung để sắp xếp theo bất kỳ cột nào
    @Override
    public List<Transaction> getSortedTransactions(String sortBy, String order) {
        Sort sort = order.equalsIgnoreCase("asc")
                ? Sort.by(Sort.Order.asc(sortBy))
                : Sort.by(Sort.Order.desc(sortBy));
        return transactionRepository.findAll(sort);
    }

    @Override
    public List<Transaction> getTransactionsByUserId(Integer userId) {
        return transactionRepository.findListTransactionByUserId(userId);
    }
}
