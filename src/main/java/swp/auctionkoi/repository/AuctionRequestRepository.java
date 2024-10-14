package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.auctionkoi.models.AuctionRequest;

import java.util.HashMap;
import java.util.Optional;

public interface AuctionRequestRepository extends JpaRepository<AuctionRequest, Integer> {
    Optional<AuctionRequest> findByAuctionId(int auctionId);
}
