package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.auctionkoi.models.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {
}
