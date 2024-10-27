package swp.auctionkoi.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import swp.auctionkoi.models.Transaction;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
//    List<Transaction> findByAuctionId(int auctionId);
    // Phương thức tìm tất cả và sắp xếp theo Sort, không cần thêm gì khác
    List<Transaction> findAll(Sort sort);
    Transaction findTopByAuctionIdAndUserIdOrderByAmountDesc(int auctionId, int userId);
}
