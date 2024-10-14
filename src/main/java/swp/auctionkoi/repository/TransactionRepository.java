package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.auctionkoi.models.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
}
