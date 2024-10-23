package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.auctionkoi.models.Transaction;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByAuctionId(int auctionId);
    Transaction findTopByAuctionIdAndUserIdOrderByAmountDesc(int auctionId, int userId);
}
