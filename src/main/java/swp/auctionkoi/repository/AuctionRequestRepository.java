package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.auctionkoi.models.AuctionRequest;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import java.util.Optional;

public interface AuctionRequestRepository extends JpaRepository<AuctionRequest, Integer> {
    Optional<AuctionRequest> findByAuctionId(int auctionId);
//    AuctionRequest findByAuctionIdForLeaderBoard(int auctionId);
    Optional<AuctionRequest> findById(int auctionId);

    List<AuctionRequest> findListAuctionRequestByUserId(int userId); //breeder

    List<AuctionRequest> findByAuctionIsNotNull();

    boolean existsAuctionsByStartTime(LocalDateTime auctionDateTime);
}
