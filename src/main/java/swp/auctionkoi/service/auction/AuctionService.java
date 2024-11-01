package swp.auctionkoi.service.auction;

import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.request.AuctionDTO;
import swp.auctionkoi.dto.respone.AuctionResponse;
import swp.auctionkoi.dto.respone.auction.AuctionJoinResponse;
import swp.auctionkoi.dto.respone.auction.AuctionResonpse;
import swp.auctionkoi.models.Auction;
import swp.auctionkoi.models.Bid;

import java.util.*;

import swp.auctionkoi.models.Auction;
import swp.auctionkoi.models.Bid;
import swp.auctionkoi.models.User;

import java.util.HashMap;
import java.util.Optional;

public interface AuctionService {
    public void startAuction(int auctionId);

    public AuctionJoinResponse joinAuction(int userId, int auctionId);

    public String checkUserParticipationInAuction(int userId, int auctionId);

    public void endAuction(int auctionId);

    public Auction getAuctionById(int auctionId);
    public List<AuctionResonpse> getListAuctionComplete();
}
