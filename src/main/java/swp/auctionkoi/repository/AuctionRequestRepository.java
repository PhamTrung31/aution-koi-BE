package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.auctionkoi.models.AuctionRequest;

import java.util.Optional;

public interface AuctionRequestRepository extends JpaRepository<AuctionRequest, Integer> {
    Optional<AuctionRequest> findById(int auctionId); // Existing method to find by auctionId

    // New method to find AuctionRequest by auction ID
    Optional<AuctionRequest> findByAuctionId(int auctionId);
}
