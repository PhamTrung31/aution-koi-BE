package swp.auctionkoi.service.auction.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.request.Auction;
import swp.auctionkoi.dto.respone.AuctionResponse;
import swp.auctionkoi.models.AuctionRequest;
import swp.auctionkoi.models.Bid;
import swp.auctionkoi.models.enums.KoiStatus;
import swp.auctionkoi.repository.*;
import swp.auctionkoi.service.auction.AuctionService;

import java.util.HashMap;
import java.util.Optional;

@Service
public class AuctionServiceImpl implements AuctionService {
    @Autowired
    private AuctionRequestRepository auctionRequestRepository;

    @Autowired
    private KoiFishRepository koiFishRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<swp.auctionkoi.models.Auction> getAuction(int id) {
        return Optional.empty();
    }

    @Override
    public AuctionResponse createAuction (Auction auctionDTO){
        AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionDTO.getAuctionId())
                .orElseThrow(() -> new IllegalArgumentException("Auction Request not found with id: " + auctionDTO.getAuctionId()));

        if (!auctionRequest.getRequestStatus().equals(KoiStatus.APPROVED)) {
            return AuctionResponse.builder()
                    .status("400")
                    .message("Auction Request is not approved")
                    .build();
        }

        swp.auctionkoi.models.Auction auction = new swp.auctionkoi.models.Auction();
        auction.setFish(auctionRequest.getFish());
        auction.setStartTime(auctionDTO.getStartTime());
        auction.setEndTime(auctionDTO.getEndTime());
        auction.setCurrentPrice(auctionRequest.getStartPrice());  // Start price comes from auction request
        auction.setStatus(KoiStatus.LISTED_FOR_AUCTION.ordinal()); // Enum representing scheduled auction status

        auctionRepository.save(auction);

        return AuctionResponse.builder()
                .status("200")
                .message("Auction created successfully")
                .auctionId(auction.getId())
                .startTime(auction.getStartTime())
                .endTime(auction.getEndTime())
                .build();
    }

    @Override
    public swp.auctionkoi.models.Auction updateAuction(swp.auctionkoi.models.Auction auction) {
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
}
