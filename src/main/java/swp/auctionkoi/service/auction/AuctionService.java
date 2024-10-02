package swp.auctionkoi.service.auction;


import swp.auctionkoi.models.Auction;

import java.util.Optional;

public interface AuctionService {
    Optional<Auction> getAuction(int id);

    Auction addAuction(Auction auction);

    Auction updateAuction(Auction auction);

    void deleteAuction(int id);
}


