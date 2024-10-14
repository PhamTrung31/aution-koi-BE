package swp.auctionkoi.service.auction.impl;

import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.Auction;
import swp.auctionkoi.models.Bid;
import swp.auctionkoi.repository.AuctionRepository;
import swp.auctionkoi.service.auction.AuctionService;
import swp.auctionkoi.service.wallet.WalletService;

import java.util.HashMap;
import java.util.Optional;

public class AuctionServiceImpl implements AuctionService {


    AuctionRepository auctionRepository;

    WalletService walletService;

    @Override
    public Optional<Auction> getAuction(int id) {
        return Optional.empty();
    }

    @Override
    public Auction addAuction(Auction auction) {
        return null;
    }

    @Override
    public Auction updateAuction(Auction auction) {
        return null;
    }

    @Override
    public void deleteAuction(int id) {

    }

    @Override
    public void viewDetailAuction(int auctionId) {

    }

    @Override
    public HashMap<Integer, Bid> viewHistoryBidAuction(int auctionId) {
        return null;
    }

    @Override
    public Auction startAuction(int auctionId) {
        return null;
    }

    @Override
    public void checkUserDepositBeforeBidding(int auctionId, int userId) {

    }

    @Override
    public void handleBidDuringAuction(int auctionId, int userId, float bidAmount) {

    }

    @Override
    public void closeAuction(int auctionId) {

    }
}
