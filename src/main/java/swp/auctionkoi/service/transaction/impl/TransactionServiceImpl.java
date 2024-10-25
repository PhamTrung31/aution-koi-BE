package swp.auctionkoi.service.transaction.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import swp.auctionkoi.models.Transaction;
import swp.auctionkoi.models.Wallet;
import swp.auctionkoi.models.enums.TransactionType;
import swp.auctionkoi.repository.TransactionRepository;
import swp.auctionkoi.repository.UserRepository;
import swp.auctionkoi.repository.WalletRepository;
import swp.auctionkoi.service.transaction.TransactionService;

import java.time.Instant;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {


    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void saveTransaction(Integer userId, Double amount) {
        // Tìm thông tin ví của người dùng
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        if (wallet != null) {
            // Tạo transaction
            Transaction transaction = Transaction.builder()
                    .member(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Người dùng không tồn tại")))
                    .transactionDate(Instant.now())
                    .transactionType(TransactionType.TOP_UP)  // Kiểu giao dịch là nạp tiền
                    .transactionFee(0.0F)  // Miễn phí giao dịch cho nạp tiền
                    .walletId(wallet.getId())  // Liên kết giao dịch với ví người dùng
                    .build();
            transactionRepository.save(transaction);
        } else {
            throw new RuntimeException("Không tìm thấy ví của người dùng này.");
        }
    }

    // Hàm chung để sắp xếp theo bất kỳ cột nào
    public List<Transaction> getSortedTransactions(String sortBy, String order) {
        Sort sort = order.equalsIgnoreCase("asc")
                ? Sort.by(Sort.Order.asc(sortBy))
                : Sort.by(Sort.Order.desc(sortBy));
        return transactionRepository.findAll(sort);
    }

}
