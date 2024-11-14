package swp.auctionkoi.service.bid;

import swp.auctionkoi.models.Bid;

import java.util.List;

public interface BidService {
    public List<Bid> getAllBidsInAuction(Integer auctionId);
    public List<Bid> getTop5BidsInAuction(Integer auctionId);
}
