package swp.auctionkoi.service.auctionrequest.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.request.AuctionDTO;
import swp.auctionkoi.dto.request.AuctionRequestDTO;
import swp.auctionkoi.dto.request.AuctionRequestUpdateDTO;
import swp.auctionkoi.dto.request.KoiFishDTO;
import swp.auctionkoi.dto.respone.AuctionRequestResponse;
import swp.auctionkoi.dto.respone.AuctionResponse;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.AuctionRequest;
import swp.auctionkoi.models.Auction;
import swp.auctionkoi.models.KoiFish;
import swp.auctionkoi.models.User;
import swp.auctionkoi.models.enums.Role;
import swp.auctionkoi.repository.*;
import swp.auctionkoi.service.auctionrequest.AuctionRequestService;
import swp.auctionkoi.models.enums.KoiStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
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

    public HashMap<Integer, AuctionRequestResponse> viewAllAuctionRequest(){
        HashMap<Integer, AuctionRequestResponse> auctionRequests = new HashMap<>();
        List<AuctionRequest> auctionRequestList = auctionRequestRepository.findAll();
        for (AuctionRequest auctionRequest : auctionRequestList){
            AuctionRequestResponse auctionRequestResponse = new AuctionRequestResponse();
            auctionRequestResponse.setId(auctionRequest.getId());
            auctionRequestResponse.setBreeder(auctionRequest.getBreeder());
            auctionRequestResponse.setFish(auctionRequest.getFish());
            auctionRequestResponse.setMethodType(auctionRequest.getMethodType());
            auctionRequestResponse.setStatus(auctionRequest.getRequestStatus().toString());

            auctionRequests.put(auctionRequest.getId(), auctionRequestResponse);
        }
        return auctionRequests;
    }

    public AuctionRequestResponse viewAuctionRequestDetail(int auctionRequestId) {
        AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_EXISTED));

        // Creating AuctionRequestResponse and setting its fields
        AuctionRequestResponse auctionRequestResponse = new AuctionRequestResponse();
        auctionRequestResponse.setId(auctionRequest.getId());
        auctionRequestResponse.setBreeder(auctionRequest.getBreeder());
        auctionRequestResponse.setFish(auctionRequest.getFish());
        auctionRequestResponse.setMethodType(auctionRequest.getMethodType());
        auctionRequestResponse.setStartPrice(auctionRequest.getStartPrice());
        auctionRequestResponse.setIncrementPrice(auctionRequest.getIncrementPrice());
        auctionRequestResponse.setStatus(auctionRequest.getRequestStatus().toString());

        return auctionRequestResponse;
    }

    public HashMap<Integer, AuctionRequestResponse> viewAllAuctionRequestsForBreeder(int breederId) {
        User user = userRepository.findById(breederId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        HashMap<Integer, AuctionRequestResponse> auctionRequests = new HashMap<>();

        List<AuctionRequest> auctionRequestList = auctionRequestRepository.findByBreederId(breederId);

        for (AuctionRequest auctionRequest : auctionRequestList) {
            AuctionRequestResponse auctionRequestResponse = new AuctionRequestResponse();
            auctionRequestResponse.setId(auctionRequest.getId());
            auctionRequestResponse.setBreeder(auctionRequest.getBreeder());
            auctionRequestResponse.setFish(auctionRequest.getFish());
            auctionRequestResponse.setMethodType(auctionRequest.getMethodType());
            auctionRequestResponse.setStatus(auctionRequest.getRequestStatus().toString());

            auctionRequests.put(auctionRequest.getId(), auctionRequestResponse);
        }

        return auctionRequests;
    }


    public AuctionRequestResponse sendAuctionRequest(AuctionRequestDTO auctionRequestDto) {
        KoiFish koiFish = koiFishRepository.findById(auctionRequestDto.getFishId())
                .orElseThrow(() -> new AppException(ErrorCode.FISH_NOT_EXISTED));

        User user = userRepository.findById(auctionRequestDto.getBreederId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if(user == null)
        {
            return
                    AuctionRequestResponse
                    .builder()
                    .status("400")
                    .message("User not found " + auctionRequestDto.getBreederId())
                    .build();
        }
        
        if(!user.getRole().equals(Role.BREEDER)){
            return AuctionRequestResponse
                    .builder()
                    .status("400")
                    .message("You are not allow to do this action ")
                    .build();
        }

        KoiFishDTO koiFishDTO = new KoiFishDTO();
        if(koiFish == null)
        {
            koiFish = new KoiFish();
            koiFish.setBreeder(user);
            koiFish.setName(koiFishDTO.getName());
            koiFish.setSex(koiFishDTO.getSex());
            koiFish.setSize(koiFishDTO.getSize());
            koiFish.setAge(koiFishDTO.getAge());
            koiFish.setDescription(koiFishDTO.getDescription());
            koiFish.setImage_Url(koiFishDTO.getImage_Url());
            koiFish.setVideo_Url(koiFishDTO.getVideo_Url());
            koiFish.setFishCreatedDate(Instant.now());
            koiFish.setStatus(KoiStatus.NEW);
            koiFishRepository.save(koiFish);
        }

        AuctionRequest auctionRequest = new AuctionRequest();
        auctionRequest.setBreeder(user);
        auctionRequest.setFish(koiFish);
        auctionRequest.setAuction(null);
        auctionRequest.setBuyOut(auctionRequestDto.getBuyOut());
        auctionRequest.setStartPrice(auctionRequestDto.getStartPrice());
        auctionRequest.setIncrementPrice(auctionRequestDto.getIncrementPrice());
        auctionRequest.setMethodType(auctionRequestDto.getMethodType());
        auctionRequest.setRequestCreatedDate(LocalDateTime.now());
        auctionRequest.setRequestUpdatedDate(LocalDateTime.now());
        auctionRequest.setRequestStatus(KoiStatus.PENDING_APPROVAL.ordinal());

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
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_EXISTED));

        KoiFish koiFish = koiFishRepository.findById(auctionRequestDTO.getFish().getId())
                .orElseThrow(() -> new AppException(ErrorCode.FISH_NOT_EXISTED));

        Auction auction = auctionRepository.findById(auctionRequestDTO.getAuction().getId())
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_NOT_EXISTED));

        User user = userRepository.findById(auctionRequestDTO.getBreeder().getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!user.getRole().equals(Role.BREEDER) || !user.getRole().equals(Role.STAFF)) {
            return AuctionRequestResponse
                    .builder()
                    .status("400")
                    .message("You are not allowed to do this action.")
                    .build();
        }

        auctionRequest.setId(auctionRequestId);
        auctionRequest.setBreeder(user);
        auctionRequest.setFish(koiFish);
        auctionRequest.setAuction(auction);
        auctionRequest.setBuyOut(auctionRequestDTO.getBuyOut());
        auctionRequest.setStartPrice(auctionRequestDTO.getStartPrice());
        auctionRequest.setIncrementPrice(auctionRequestDTO.getIncrementPrice());
        auctionRequest.setMethodType(auctionRequestDTO.getMethodType());
        auctionRequest.setRequestUpdatedDate(LocalDateTime.now());
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

    public AuctionRequestResponse cancelAuctionRequest(int auctionRequestId, int breederID){
        AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_EXISTED));

        User breeder = userRepository.findById(breederID)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!breeder.getRole().equals(Role.BREEDER)) {
            return AuctionRequestResponse
                    .builder()
                    .status("400")
                    .message("You are not authorized to approve auction requests.")
                    .build();
        }

        if (!auctionRequest.getRequestStatus().equals(KoiStatus.CANCELED) || !auctionRequest.getRequestStatus().equals(KoiStatus.LISTED_FOR_AUCTION)) {
            return AuctionRequestResponse
                    .builder()
                    .status("400")
                    .message("The auction request has already been processed.")
                    .build();
        }

        auctionRequest.setRequestStatus(KoiStatus.CANCELED.ordinal());
        auctionRequest.setRequestUpdatedDate(LocalDateTime.now());

        auctionRequestRepository.save(auctionRequest);

        return AuctionRequestResponse
                .builder()
                .status("200")
                .message("Auction request rejected successfully!")
                .id(auctionRequest.getId())
                .methodType(auctionRequest.getMethodType())
                .breeder(auctionRequest.getBreeder())
                .fish(auctionRequest.getFish())
                .startPrice(auctionRequest.getStartPrice())
                .incrementPrice(auctionRequest.getIncrementPrice())
                .build();
    }

    public AuctionRequestResponse approveAuctionRequest(int auctionRequestId, int staffId, LocalDateTime auctionDateTime) {

        boolean isConflict = auctionRepository.existsAuctionsByStartTime(auctionDateTime);

        AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_EXISTED));

        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!staff.getRole().equals(Role.STAFF)) {
            return AuctionRequestResponse
                    .builder()
                    .status("400")
                    .message("You are not authorized to approve auction requests.")
                    .build();
        }

        if (!auctionRequest.getRequestStatus().equals(KoiStatus.PENDING_APPROVAL)) {
            return AuctionRequestResponse
                    .builder()
                    .status("400")
                    .message("The auction request has already been processed.")
                    .build();
        }

        if (auctionDateTime.isBefore(LocalDateTime.now()) || isConflict) {
            return AuctionRequestResponse
                    .builder()
                    .status("400")
                    .message("The auction date and time are not available. Please choose a different time.")
                    .build();
        }

        auctionRequest.setRequestStatus(KoiStatus.APPROVED.ordinal());
        auctionRequest.setRequestUpdatedDate(LocalDateTime.now());

        auctionRequestRepository.save(auctionRequest);

        return AuctionRequestResponse
                .builder()
                .status("200")
                .message("Auction request approved successfully!")
                .id(auctionRequest.getId())
                .methodType(auctionRequest.getMethodType())
                .breeder(auctionRequest.getBreeder())
                .fish(auctionRequest.getFish())
                .startPrice(auctionRequest.getStartPrice())
                .incrementPrice(auctionRequest.getIncrementPrice())
                .build();
    }

    public AuctionRequestResponse rejectAuctionRequest(int auctionRequestId, int staffId) {

        AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_EXISTED));

        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!staff.getRole().equals(Role.STAFF)) {
            return AuctionRequestResponse
                    .builder()
                    .status("400")
                    .message("You are not authorized to approve auction requests.")
                    .build();
        }

        if (!auctionRequest.getRequestStatus().equals(KoiStatus.REJECTED)) {
            return AuctionRequestResponse
                    .builder()
                    .status("400")
                    .message("The auction request has already been processed.")
                    .build();
        }

        auctionRequest.setRequestStatus(KoiStatus.REJECTED.ordinal());
        auctionRequest.setRequestUpdatedDate(LocalDateTime.now());

        auctionRequestRepository.save(auctionRequest);

        return AuctionRequestResponse
                .builder()
                .status("200")
                .message("Auction request rejected successfully!")
                .id(auctionRequest.getId())
                .methodType(auctionRequest.getMethodType())
                .breeder(auctionRequest.getBreeder())
                .fish(auctionRequest.getFish())
                .startPrice(auctionRequest.getStartPrice())
                .incrementPrice(auctionRequest.getIncrementPrice())
                .build();

    }

    public AuctionRequestResponse sendRequestUpdateDetailAuction(int auctionId, AuctionRequestUpdateDTO auctionRequestUpdateDTO) {

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_NOT_EXISTED));

        User user = userRepository.findById(auctionRequestUpdateDTO.getBreeder().getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (auction.getStatus().equals(KoiStatus.LISTED_FOR_AUCTION) || auction.getStatus().equals(KoiStatus.CANCELED)) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        AuctionRequest auctionRequest = new AuctionRequest();
        auctionRequest.setId(auction.getId());
        auctionRequest.setBreeder(auctionRequestUpdateDTO.getBreeder());
        auctionRequest.setFish(auctionRequestUpdateDTO.getFish());
        auctionRequest.setBuyOut(auctionRequestUpdateDTO.getBuyOut());
        auctionRequest.setStartPrice(auctionRequestUpdateDTO.getStartPrice());
        auctionRequest.setIncrementPrice(auctionRequestUpdateDTO.getIncrementPrice());
        auctionRequest.setMethodType(auctionRequestUpdateDTO.getMethodType());
        auctionRequest.setRequestCreatedDate(LocalDateTime.now());
        auctionRequest.setRequestStatus(KoiStatus.PENDING_APPROVAL.ordinal());

        auctionRequestRepository.save(auctionRequest);

        AuctionRequestResponse response = new AuctionRequestResponse();
        response.setId(auctionRequest.getId());
        response.setBreeder(auctionRequest.getBreeder());
        response.setFish(auctionRequest.getFish());
        response.setMethodType(auctionRequest.getMethodType());
        response.setStatus(auctionRequest.getRequestStatus().toString());

        return response;

    }

    public AuctionResponse approveRequestUpdateAuction(int auctionRequestId, int staffId) {

        AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_EXISTED));

        if (!auctionRequest.getRequestStatus().equals(KoiStatus.PENDING_APPROVAL)) {
            throw new AppException(ErrorCode.AUCTION_NOT_APPROVE);
        }

        Auction auction = auctionRepository.findById(auctionRequest.getId())
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_NOT_EXISTED));

        auction.setId(auctionRequest.getAuction().getId());
        auction.setFish(auctionRequest.getFish());

        auctionRequest.setRequestStatus(KoiStatus.APPROVED.ordinal());
        auctionRequest.setRequestUpdatedDate(LocalDateTime.now());

        auctionRepository.save(auction);
        auctionRequestRepository.save(auctionRequest);

        AuctionResponse response = new AuctionResponse();
        response.setAuctionId(auction.getId());
        response.setStartTime(auction.getStartTime());
        response.setEndTime(auction.getEndTime());
        response.setCurrentPrice(auction.getCurrentPrice());
        response.setStatus(auction.getStatus().toString());

        return response;
    }

}
