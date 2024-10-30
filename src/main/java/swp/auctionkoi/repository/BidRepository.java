package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import swp.auctionkoi.models.Auction;
import swp.auctionkoi.models.AuctionRequest;
import swp.auctionkoi.models.Bid;
import swp.auctionkoi.models.User;

import java.util.List;
import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Integer> {

    Bid findTopByAuctionIdAndUserIdOrderByBidAmountDesc(int auctionId, Integer id);
    boolean existsByAuctionAndUser(Auction auction, User user);
    @Query(value = "SELECT * FROM Bids WHERE auction_id = :auctionId ORDER BY bid_amount DESC LIMIT 5", nativeQuery = true)
    List<Bid> findTop5HighestBidsByAuctionId(@Param("auctionId") Integer auctionId);

    public List<Bid> getListBidByAuctionId(int auctionId);

    @Query(value = "SELECT * FROM Bids WHERE auction_id = :auctionId ORDER BY bid_amount DESC, bid_created_date ASC LIMIT 1", nativeQuery = true)
    public Bid getBidHighestAmountAndEarliestInAuction(@Param("auctionId") Integer auctionId);

    List<Bid> findListBidByAuctionId(int auctionId);
}
