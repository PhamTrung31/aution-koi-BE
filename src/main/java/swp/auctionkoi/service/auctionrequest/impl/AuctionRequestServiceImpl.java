package swp.auctionkoi.service.auctionrequest.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.request.AuctionRequestDTO;
import swp.auctionkoi.dto.respone.AuctionRequestResponse;
import swp.auctionkoi.models.AuctionRequest;
import swp.auctionkoi.models.Auction;
import swp.auctionkoi.models.KoiFish;
import swp.auctionkoi.models.User;
import swp.auctionkoi.models.enums.Role;
import swp.auctionkoi.repository.AuctionRepository;
import swp.auctionkoi.repository.AuctionRequestRepository;
import swp.auctionkoi.repository.KoiFishRepository;
import swp.auctionkoi.repository.UserRepository;
import swp.auctionkoi.service.auctionrequest.AuctionRequestService;

import java.time.Instant;

@Service
public class AuctionRequestServiceImpl implements AuctionRequestService {
    @Autowired
    private AuctionRequestRepository auctionRequestRepository;

    @Autowired
    private KoiFishRepository koiFishRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public AuctionRequestResponse sendAuctionRequest(AuctionRequestDTO auctionRequestDto) {
        KoiFish koiFish = koiFishRepository.findById(auctionRequestDto.getFishId())
                .orElseThrow(() -> new IllegalArgumentException("KoiFish not found with id: " + auctionRequestDto.getFishId()));

        Auction auction = auctionRepository.findById(auctionRequestDto.getAuctionId())
                .orElseThrow(() -> new IllegalArgumentException("Auction not found with id: " + auctionRequestDto.getAuctionId()));

        User user = userRepository.findById(auctionRequestDto.getBreederId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + auctionRequestDto.getBreederId()));
        if(koiFish == null)
        {
            return AuctionRequestResponse
                    .builder()
                    .status("400")
                    .message("KoiFish not found with id: " + auctionRequestDto.getFishId())
                    .build();
        }

        if(auction == null)
        {
            return AuctionRequestResponse
                    .builder()
                    .status("400")
                    .message("Auction not found with id: " + auctionRequestDto.getAuctionId())
                    .build();
        }
        if(user == null)
        {
            return AuctionRequestResponse
                    .builder()
                    .status("400")
                    .message("User not found " + auctionRequestDto.getAuctionId())
                    .build();
        }
        
        if(!user.getRole().equals(Role.BREEDER)){
            return AuctionRequestResponse
                    .builder()
                    .status("400")
                    .message("You are not allow to do this action ")
                    .build();
        }

        AuctionRequest auctionRequest = new AuctionRequest();
        auctionRequest.setBreeder(user);
        auctionRequest.setFish(koiFish);
        auctionRequest.setAuction(auction);
        auctionRequest.setBuyOut(auctionRequestDto.getBuyOut());
        auctionRequest.setStartPrice(auctionRequestDto.getStartPrice());
        auctionRequest.setIncrementPrice(auctionRequestDto.getIncrementPrice());
        auctionRequest.setMethodType(auctionRequestDto.getMethodType());
        auctionRequest.setRequestCreatedDate(Instant.now());
        auctionRequest.setRequestUpdatedDate(Instant.now());
        auctionRequest.setRequestStatus(auctionRequestDto.getRequestStatus());

        auctionRequestRepository.save(auctionRequest);

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
//    public AuctionResponseUpdate updateAuctionReuest(AuctionRequestUpdateDTO auctionRequestUpdateDTO){
//        AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestUpdateDTO.getFish().getId())
//                .orElseThrow(() -> new IllegalArgumentException("AuctionRequest not found with id: " + auctionRequestUpdateDTO.getId()));
//
//        KoiFish koiFish = koiFishRepository.findById(auctionRequestUpdateDTO.getFish().getId())
//                .orElseThrow(() -> new IllegalArgumentException("KoiFish not found with id: " + auctionRequestUpdateDTO.getFish().getId()));
//
//        Auction auction = auctionRepository.findById(auctionRequestUpdateDTO.getAuction().getId())
//                .orElseThrow(() -> new IllegalArgumentException("Auction not found with id: " + auctionRequestUpdateDTO.getAuction().getId()));
//
//        if(auction == null)
//        {
//            return AuctionRequestResponse
//                    .builder()
//                    .status("400")
//                    .message("Auction not found with id: " + AuctionRequestUpdateDTO.getAuction().getId())
//                    .build();
//        }
//
//        return AuctionResponseUpdate.builder().build();
//    }

}
