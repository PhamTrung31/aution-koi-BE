package swp.auctionkoi.service.auction.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp.auctionkoi.models.Auction;
import swp.auctionkoi.models.Transaction;
import swp.auctionkoi.models.User;
import swp.auctionkoi.models.Wallet;
import swp.auctionkoi.repository.AuctionRepository;
import swp.auctionkoi.repository.TransactionRepository;
import swp.auctionkoi.repository.UserRepository;
import swp.auctionkoi.repository.WalletRepository;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AuctionServiceImpl {

    @Autowired
    private AuctionRepository auctionRepository;

    public Optional<Auction> getAuction(int id) {
        return auctionRepository.findById(id);
    }


    public Auction addAuction(Auction auction) {
        return auctionRepository.save(auction);
    }


    public Auction updateAuction(Auction auction) {
        Optional<Auction> existingAuction = auctionRepository.findById(auction.getId());
        if (existingAuction.isPresent()) {
            return auctionRepository.save(auction);
        }
        throw new RuntimeException("Auction not found");
    }


    public void deleteAuction(int id) {
        Optional<Auction> existingAuction = auctionRepository.findById(id);
        if (existingAuction.isPresent()) {
            auctionRepository.deleteById(id);
        } else {
            throw new RuntimeException("Auction not found");
        }
    }
}
