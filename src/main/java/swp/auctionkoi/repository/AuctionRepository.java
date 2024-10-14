package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.auctionkoi.models.Auction;

import java.time.Instant;
import java.time.LocalDateTime;

public interface AuctionRepository extends JpaRepository<Auction, Integer> {
    boolean existsAuctionsByStartTime(LocalDateTime auctionDateTime);
}
