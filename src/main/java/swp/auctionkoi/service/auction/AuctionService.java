package swp.auctionkoi.service.auction;

import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.request.Auction;
import swp.auctionkoi.dto.respone.AuctionResponse;
import swp.auctionkoi.models.Bid;

import java.util.HashMap;
import java.util.Optional;

public interface AuctionService {
    public Optional<swp.auctionkoi.models.Auction> getAuction(int id);
    public Optional<AuctionResponse> createAuction (Auction auctionDTO);
    public swp.auctionkoi.models.Auction updateAuction(swp.auctionkoi.models.Auction auction);
    public void deleteAuction(int id);
    public void viewDetailAuction(int auctionId);
    public HashMap<Integer, Bid> viewHistoryBidAuction(int auctionId);
}
