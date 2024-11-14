package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import swp.auctionkoi.models.Auction;
import swp.auctionkoi.models.AuctionRequest;
import swp.auctionkoi.models.enums.AuctionStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction, Integer> {

    Optional<Auction> getAuctionById(int auctionId);

    @Query(value = "SELECT * FROM Auctions WHERE status = :status", nativeQuery = true)
    List<Auction> getListAuctionCompleteByStatus(@Param("status") int status);

    int countByStatus(AuctionStatus status);
}
