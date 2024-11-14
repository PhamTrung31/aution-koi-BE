package swp.auctionkoi.service.auction;

import swp.auctionkoi.dto.respone.auction.AuctionJoinResponse;
import swp.auctionkoi.dto.respone.auction.AuctionHistoryResponse;
import swp.auctionkoi.models.Auction;

import java.util.*;

public interface AuctionService {
    public void startAuction(int auctionId);

    public AuctionJoinResponse joinAuction(int userId, int auctionId);

    public String checkUserParticipationInAuction(int userId, int auctionId);

    public void endAuction(int auctionId);

    public Auction getAuctionById(int auctionId);
    public List<AuctionHistoryResponse> getListAuctionComplete();
}
