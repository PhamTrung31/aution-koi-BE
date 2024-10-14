package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.auctionkoi.models.Auction;
import swp.auctionkoi.models.Bid;
import swp.auctionkoi.models.User;

public interface BidRepository extends JpaRepository<Bid, Integer> {
    boolean existsByAuctionAndBidder(Auction auction, User user);
    Bid findByAuctionAndBidder(Auction auction, User user);
}
