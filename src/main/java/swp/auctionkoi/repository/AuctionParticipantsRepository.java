package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.auctionkoi.models.AuctionParticipants;

import java.util.List;
import java.util.Optional;

public interface AuctionParticipantsRepository extends JpaRepository<AuctionParticipants, Integer> {
    boolean existsByAuctionIdAndUserId(int auctionId, int userId);
    AuctionParticipants findByAuctionIdAndUserId(int auctionId, int userId);

    List<AuctionParticipants> findListAuctionParticipantsByAuctionId(int auctionId);

    Optional<AuctionParticipants> findByAuctionId(int auctionId);
}
