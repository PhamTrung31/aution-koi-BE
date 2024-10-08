package swp.auctionkoi.service.auctionrequest.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.request.AuctionRequestDTO;
import swp.auctionkoi.dto.request.AuctionRequestUpdateDTO;
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

    public AuctionRequestResponse updateAuctionRequest (int auctionRequestId, AuctionRequestUpdateDTO auctionRequestDTO)
    {
        AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId)
                .orElseThrow(() -> new IllegalArgumentException("AuctionRequest not found with id: " + auctionRequestId));

        KoiFish koiFish = koiFishRepository.findById(auctionRequestDTO.getFish().getId())
                .orElseThrow(() -> new IllegalArgumentException("KoiFish not found with id: " + auctionRequestDTO.getFish().getId()));

        Auction auction = auctionRepository.findById(auctionRequestDTO.getAuction().getId())
                .orElseThrow(() -> new IllegalArgumentException("Auction not found with id: " + auctionRequestDTO.getAuction().getId()));

        User user = userRepository.findById(auctionRequestDTO.getBreeder().getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + auctionRequestDTO.getBreeder().getId()));

        if (!user.getRole().equals(Role.BREEDER) || !user.getRole().equals(Role.STAFF)) {
            return AuctionRequestResponse
                    .builder()
                    .status("400")
                    .message("You are not allowed to do this action.")
                    .build();
        }

        auctionRequest.setBreeder(user);
        auctionRequest.setFish(koiFish);
        auctionRequest.setAuction(auction);
        auctionRequest.setBuyOut(auctionRequestDTO.getBuyOut());
        auctionRequest.setStartPrice(auctionRequestDTO.getStartPrice());
        auctionRequest.setIncrementPrice(auctionRequestDTO.getIncrementPrice());
        auctionRequest.setMethodType(auctionRequestDTO.getMethodType());
        auctionRequest.setRequestUpdatedDate(Instant.now());
        auctionRequest.setRequestStatus(auctionRequestDTO.getRequestStatus());

        auctionRequestRepository.save(auctionRequest);

        return AuctionRequestResponse
                .builder()
                .status("200")
                .message("Auction request updated successfully!")
                .id(auctionRequest.getId())
                .methodType(auctionRequest.getMethodType())
                .breeder(auctionRequest.getBreeder())
                .fish(auctionRequest.getFish())
                .startPrice(auctionRequest.getStartPrice())
                .incrementPrice(auctionRequest.getIncrementPrice())
                .build();
    }

//    public AuctionRequestResponse approveAuctionRequest(int auctionRequestId, int staffId, Instant auctionDateTime) {
//        // Fetch the auction request
//        AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId)
//                .orElseThrow(() -> new IllegalArgumentException("AuctionRequest not found with id: " + auctionRequestId));
//
//        // Fetch the staff user
//        User staff = userRepository.findById(staffId)
//                .orElseThrow(() -> new IllegalArgumentException("Staff not found with id: " + staffId));
//
//        // Check if the user is authorized to approve (assumes the staff have a specific role)
//        if (!staff.getRole().equals(Role.STAFF)) {
//            return AuctionRequestResponse
//                    .builder()
//                    .status("400")
//                    .message("You are not authorized to approve auction requests.")
//                    .build();
//        }
//
//        // Validate that the auction request is pending and not already approved or rejected
//        if (!auctionRequest.getRequestStatus().equals(RequestStatus.PENDING)) {
//            return AuctionRequestResponse
//                    .builder()
//                    .status("400")
//                    .message("The auction request has already been processed.")
//                    .build();
//        }
//
//        // Check if the auctionDateTime is valid (e.g., not in the past or conflicting with another auction)
//        if (auctionDateTime.isBefore(Instant.now())) {
//            return AuctionRequestResponse
//                    .builder()
//                    .status("400")
//                    .message("The auction date and time cannot be in the past.")
//                    .build();
//        }
//
//        // Optional: You could add logic to check for auction conflicts, e.g.,
//        // `auctionRepository.existsByAuctionDateTime(auctionDateTime)` to prevent overlaps.
//
//        // Approve the auction request and set the date and time
//        auctionRequest.setRequestStatus(RequestStatus.APPROVED);
//        auctionRequest.setAuctionDateTime(auctionDateTime);
//        auctionRequest.setRequestUpdatedDate(Instant.now());
//
//        auctionRequestRepository.save(auctionRequest);
//
//        // Return success response
//        return AuctionRequestResponse
//                .builder()
//                .status("200")
//                .message("Auction request approved successfully!")
//                .id(auctionRequest.getId())
//                .methodType(auctionRequest.getMethodType())
//                .auctionDateTime(auctionRequest.getAuctionDateTime())
//                .breeder(auctionRequest.getBreeder())
//                .fish(auctionRequest.getFish())
//                .startPrice(auctionRequest.getStartPrice())
//                .incrementPrice(auctionRequest.getIncrementPrice())
//                .build();
//    }

}
