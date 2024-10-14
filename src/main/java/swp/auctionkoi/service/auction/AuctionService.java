package swp.auctionkoi.service.auction;

import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.request.AuctionDTO;
import swp.auctionkoi.dto.respone.AuctionResponse;
import swp.auctionkoi.models.Auction;
import swp.auctionkoi.models.Bid;

import java.util.HashMap;
import java.util.Optional;
import swp.auctionkoi.models.Auction;
import swp.auctionkoi.models.Bid;
import swp.auctionkoi.models.User;

import java.util.HashMap;
import java.util.Optional;

public interface AuctionService {
    public Optional<Auction> getAuction(int id);
    AuctionResponse createAuction(AuctionDTO auctionDTO);
    public Auction updateAuction(Auction auction);
    public void deleteAuction(int id);

    public void viewDetailAuction(int auctionId);
    public HashMap<Integer, Bid> viewHistoryBidAuction(int auctionId);


    public Auction startAuction(int auctionId);


    public void checkUserDepositBeforeBidding(int auctionId, int userId);
    public void handleBidDuringAuction(int auctionId, int userId, float bidAmount);
    public void closeAuction(int auctionId);
    public AuctionJoinResponse JoinAuction(int userId, int auctionId);
    public void endAuction(int auctionId);

}
