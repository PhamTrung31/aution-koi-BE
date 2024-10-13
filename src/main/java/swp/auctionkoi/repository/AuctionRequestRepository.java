package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.auctionkoi.models.AuctionRequest;

import java.util.List;

public interface AuctionRequestRepository extends JpaRepository<AuctionRequest, Integer> {
    List<AuctionRequest> findByBreederId(int breederId);
}
