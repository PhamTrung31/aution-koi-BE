package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.auctionkoi.models.Bid;

import java.util.List;
import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Integer> {

    List<Bid> findByAuctionIdAndUserId(int auctionId, Integer id);
    Optional<Bid> findTopByAuctionIdAndUserIdOrderByBidAmountDesc(int auctionId, Integer id);
}
