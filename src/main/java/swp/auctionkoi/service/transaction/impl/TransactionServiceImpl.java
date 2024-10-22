package swp.auctionkoi.service.transaction.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp.auctionkoi.models.Transaction;
import swp.auctionkoi.models.Wallet;
import swp.auctionkoi.models.enums.TransactionType;
import swp.auctionkoi.repository.TransactionRepository;
import swp.auctionkoi.repository.UserRepository;
import swp.auctionkoi.repository.WalletRepository;
import swp.auctionkoi.service.transaction.TransactionService;

import java.time.Instant;

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
                    .transactionFee(0.0)  // Miễn phí giao dịch cho nạp tiền
                    .walletId(wallet.getId())  // Liên kết giao dịch với ví người dùng
                    .build();
            transactionRepository.save(transaction);
        } else {
            throw new RuntimeException("Không tìm thấy ví của người dùng này.");
        }
    }
}
