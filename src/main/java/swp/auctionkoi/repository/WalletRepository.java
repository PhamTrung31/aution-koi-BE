package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.auctionkoi.models.Wallet;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {
    Wallet findByUserId(int userId);
}
