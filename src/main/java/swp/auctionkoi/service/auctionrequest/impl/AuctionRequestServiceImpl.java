package swp.auctionkoi.service.auctionrequest.impl;

import org.springframework.beans.factory.annotation.Autowired;
import swp.auctionkoi.dto.request.AuctionRequestDTO;
import swp.auctionkoi.dto.request.AuctionRequestUpdateDTO;
import swp.auctionkoi.dto.respone.AuctionRequestResponse;
import swp.auctionkoi.dto.respone.AuctionResponseUpdate;
import swp.auctionkoi.models.AuctionRequest;
import swp.auctionkoi.models.Auction;
import swp.auctionkoi.models.KoiFish;
import swp.auctionkoi.repository.AuctionRepository;
import swp.auctionkoi.repository.AuctionRequestRepository;
import swp.auctionkoi.repository.KoiFishRepository;
import swp.auctionkoi.service.auctionrequest.AuctionRequestService;

import java.time.Instant;

public class AuctionRequestServiceImpl implements AuctionRequestService {
    @Autowired
    private AuctionRequestRepository auctionRequestRepository;

    @Autowired
    private KoiFishRepository koiFishRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    public AuctionRequestResponse sendAuctionRequest(AuctionRequestDTO auctionRequestDto) {
        // Validate the request
        KoiFish koiFish = koiFishRepository.findById(auctionRequestDto.getFish().getId())
                .orElseThrow(() -> new IllegalArgumentException("KoiFish not found with id: " + auctionRequestDto.getFish().getId()));

        // Validate Auction Data
        Auction auction = auctionRepository.findById(auctionRequestDto.getAuction().getId())
                .orElseThrow(() -> new IllegalArgumentException("Auction not found with id: " + auctionRequestDto.getFish().getId()));

        if(koiFish == null)
        {
            return AuctionRequestResponse
                    .builder()
                    .status("400")
                    .message("KoiFish not found with id: " + auctionRequestDto.getFish().getId())
                    .build();
        }

        if(auction == null)
        {
            return AuctionRequestResponse
                    .builder()
                    .status("400")
                    .message("Auction not found with id: " + auctionRequestDto.getFish().getId())
                    .build();
        }

        // Create a new Auction Request entity
        AuctionRequest auctionRequest = new AuctionRequest();
        auctionRequest.setBreeder(auctionRequestDto.getBreeder());
        auctionRequest.setFish(auctionRequestDto.getFish());
        auctionRequest.setAuction(auctionRequestDto.getAuction());
        auctionRequest.setBuyOut(auctionRequestDto.getBuyOut());
        auctionRequest.setStartPrice(auctionRequestDto.getStartPrice());
        auctionRequest.setIncrementPrice(auctionRequestDto.getIncrementPrice());
        auctionRequest.setMethodType(auctionRequestDto.getMethodType());
        auctionRequest.setRequestCreatedDate(Instant.now());
        auctionRequest.setRequestUpdatedDate(Instant.now());
        auctionRequest.setRequestStatus(auctionRequestDto.getRequestStatus());  // Set initial status to Pending

        // Save the auction request to the database
        auctionRequestRepository.save(auctionRequest);
        // You could trigger other processes here, like sending notifications, logging, etc.

        return AuctionRequestResponse
                .builder()
                .status("200")
                .message("Send Request Successfully !")
                .id(auctionRequest.getId())
                .methodType(auctionRequest.getMethodType())
                .breeder(auctionRequest.getBreeder())
                .fish(auctionRequest.getFish())
                .startPrice(auctionRequest.getStartPrice())
                .incrementPrice(auctionRequest.getIncrementPrice())
                .build();
    }
    public AuctionResponseUpdate updateAuctionReuest(AuctionRequestUpdateDTO auctionRequestUpdateDTO){
        return AuctionResponseUpdate.builder().build();
    }

}
