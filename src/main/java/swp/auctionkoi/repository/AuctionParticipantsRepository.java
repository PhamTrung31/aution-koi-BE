package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.auctionkoi.models.AuctionParticipants;

import java.util.List;
import java.util.Optional;

public interface AuctionParticipantsRepository extends JpaRepository<AuctionParticipants, Integer> {

    List<AuctionParticipants> findByAuctionId(int auctionId);

    AuctionParticipants findByAuctionIdAndUserId(int auctionId, int userId);
}
