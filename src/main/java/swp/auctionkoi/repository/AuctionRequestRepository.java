package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.auctionkoi.models.AuctionRequest;

public interface AuctionRequestRepository extends JpaRepository<AuctionRequest, Integer> {
}
