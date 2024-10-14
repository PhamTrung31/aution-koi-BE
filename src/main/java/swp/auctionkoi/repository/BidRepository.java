package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.auctionkoi.models.Auction;
import swp.auctionkoi.models.Bid;
import swp.auctionkoi.models.User;

import java.util.List;
import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Integer> {

    List<Bid> findByAuctionIdAndUserId(int auctionId, Integer id);
    Optional<Bid> findTopByAuctionIdAndUserIdOrderByBidAmountDesc(int auctionId, Integer id);
    boolean existsByAuctionAndBidder(Auction auction, User user);
    Bid findByAuctionAndBidder(Auction auction, User user);
}
