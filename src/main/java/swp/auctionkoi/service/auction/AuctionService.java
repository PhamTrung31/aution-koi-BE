package swp.auctionkoi.service.auction;

import swp.auctionkoi.dto.respone.auction.AuctionJoinResponse;
import swp.auctionkoi.models.Auction;
import swp.auctionkoi.models.Bid;
import swp.auctionkoi.models.User;

import java.util.HashMap;
import java.util.Optional;

public interface AuctionService {
    public AuctionJoinResponse JoinAuction(int userId, int auctionId);
    public void endAuction(int auctionId);

}
