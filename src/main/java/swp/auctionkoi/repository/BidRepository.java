package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.auctionkoi.models.Bid;

public interface BidRepository extends JpaRepository<Bid, Integer> {
}
