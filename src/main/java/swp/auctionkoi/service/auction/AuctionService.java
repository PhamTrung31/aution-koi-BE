package swp.auctionkoi.service.auction;

import swp.auctionkoi.models.Auction;
import swp.auctionkoi.models.Bid;

import java.util.HashMap;
import java.util.Optional;

public interface AuctionService {
    public Optional<Auction> getAuction(int id);
    public Auction addAuction(Auction auction);
    public Auction updateAuction(Auction auction);
    public void deleteAuction(int id);

    public void viewDetailAuction(int auctionId);
    public HashMap<Integer, Bid> viewHistoryBidAuction(int auctionId);


    public Auction startAuction(int auctionId);


    public void checkUserDepositBeforeBidding(int auctionId, int userId);
    public void handleBidDuringAuction(int auctionId, int userId, float bidAmount);
    public void closeAuction(int auctionId);
}
