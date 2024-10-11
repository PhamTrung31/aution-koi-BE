package swp.auctionkoi.service.auction.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.request.AuctionDTO;
import swp.auctionkoi.dto.respone.AuctionResponse;
import swp.auctionkoi.models.Auction;
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
    public Optional<Auction> getAuction(int id) {
        return Optional.empty();
    }

    @Override
    public AuctionResponse createAuction (AuctionDTO auctionDTO){
        AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionDTO.getAuctionRequestId())
                .orElseThrow(() -> new IllegalArgumentException("Auction Request not found with id: " + auctionDTO.getAuctionRequestId()));

        if (!auctionRequest.getRequestStatus().equals(KoiStatus.APPROVED)) {
            return AuctionResponse.builder()
                    .status("400")
                    .message("Auction Request is not approved")
                    .build();
        }

//        boolean isTimeConflict = auctionRepository.existsByStartTimeBetweenOrEndTimeBetween(
//                auctionDTO.getStartTime(), auctionDTO.getEndTime(),
//                auctionDTO.getStartTime(), auctionDTO.getEndTime());
//
//        if (isTimeConflict) {
//            return AuctionResponse.builder()
//                    .status("400")
//                    .message("The selected auction time conflicts with another auction")
//                    .build();
//        }]

        Auction auction = new Auction();
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
}
