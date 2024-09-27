package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.auctionkoi.models.Auction;

public interface AuctionRepository extends JpaRepository<Auction, Integer> {
}
