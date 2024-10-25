package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import swp.auctionkoi.models.Auction;
import swp.auctionkoi.models.AuctionRequest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction, Integer> {

    Optional<Auction> getAuctionById(int auctionId);
    @Query(value = "SELECT * FROM Auctions WHERE status = 2", nativeQuery = true)
    Auction getAuctionByStatusInProcess();
}
