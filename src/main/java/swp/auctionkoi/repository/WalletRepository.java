package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swp.auctionkoi.models.User;
import swp.auctionkoi.models.Wallet;

import java.util.Optional;

@Repository
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {
//    @Query("SELECT w FROM Wallet w WHERE w.member.id = :userId")
//    Optional<Wallet> findByUserId(@Param("userId") int userId); // Query to find a wallet by user ID
    Optional<Wallet> findByUserId(int userId);
}



