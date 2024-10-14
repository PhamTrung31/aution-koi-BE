package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.auctionkoi.models.AuctionParticipant;

public interface AuctionParticipantsRepository extends JpaRepository<AuctionParticipant, Integer> {
    boolean existsByAuctionIdAndUserId(int auctionId, int userId);
    AuctionParticipant findByAuctionIdAndUserId(int auctionId, int userId);
}
