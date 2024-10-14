package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.auctionkoi.models.AuctionParticipant;

import java.util.List;
import java.util.Optional;

public interface AuctionParticipantsRepository extends JpaRepository<AuctionParticipant, Integer> {
    boolean existsByAuctionIdAndUserId(int auctionId, int userId);
    AuctionParticipant findByAuctionIdAndUserId(int auctionId, int userId);

    List<AuctionParticipant> findByAuctionId(int auctionId);
}
