package swp.auctionkoi.service.auctionrequest.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.request.AuctionRequestDTO;
import swp.auctionkoi.dto.request.AuctionRequestResponseData;
import swp.auctionkoi.dto.respone.AuctionRequestResponse;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.mapper.AuctionRequestMapper;
import swp.auctionkoi.models.AuctionRequest;
import swp.auctionkoi.models.KoiFish;
import swp.auctionkoi.models.User;
import swp.auctionkoi.models.enums.AuctionRequestStatus;
import swp.auctionkoi.models.enums.Role;
import swp.auctionkoi.repository.*;
import swp.auctionkoi.service.auctionrequest.AuctionRequestService;
import swp.auctionkoi.models.enums.KoiStatus;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class AuctionRequestServiceImpl implements AuctionRequestService {

    AuctionRequestRepository auctionRequestRepository;

    KoiFishRepository koiFishRepository;

    AuctionRepository auctionRepository;

    UserRepository userRepository;
    private final AuctionRequestMapper auctionRequestMapper;

    public AuctionRequestResponse sendAuctionRequest(AuctionRequestDTO auctionRequestDTO) {
        User user = userRepository.findById(auctionRequestDTO.getBreederId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        KoiFish fish = koiFishRepository.findById(auctionRequestDTO.getFishId()).orElseThrow(() -> new AppException(ErrorCode.FISH_NOT_EXISTED));
        checkRequest(auctionRequestDTO, user, fish);

        AuctionRequest auctionRequest = saveRequest(auctionRequestDTO, user, fish);

        AuctionRequestResponse auctionRequestResponse = AuctionRequestResponse.builder()
                .success(true)
                .message("Your request has been sent successfully.")
                .data(auctionRequest)
                .build();

        return auctionRequestResponse;
    }

    public AuctionRequestResponse updateAuctionRequestForBreeder(int auctionRequestId, AuctionRequestDTO auctionRequestDTO) {
        AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId).orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_EXISTED));
        User user = userRepository.findById(auctionRequestDTO.getBreederId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        KoiFish fish = koiFishRepository.findById(auctionRequestDTO.getFishId()).orElseThrow(() -> new AppException(ErrorCode.FISH_NOT_EXISTED));

        checkRequest(auctionRequestDTO, user, fish);

        updateRequest(auctionRequest, auctionRequestDTO, user, fish);

        AuctionRequestResponse auctionRequestResponse = AuctionRequestResponse.builder()
                .success(true)
                .message("Your request has been update successfully.")
                .data(auctionRequest)
                .build();
        return auctionRequestResponse;
    }


    @Override
    public HashMap<Integer, AuctionRequestResponseData> viewAllAuctionRequest(){
        HashMap<Integer, AuctionRequestResponseData> auctionRequests = new HashMap<>();
        List<AuctionRequest> auctionRequestList = auctionRequestRepository.findAll();
        for (AuctionRequest auctionRequest : auctionRequestList){
            auctionRequests.put(auctionRequest.getId(), auctionRequestMapper.toAuctionRequestResponseData(auctionRequest));
        }
        return auctionRequests;
    }

    public AuctionRequestResponseData viewAuctionRequestDetail(int auctionRequestId) {
        AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_EXISTED));

        AuctionRequestResponseData auctionRequestResponse = auctionRequestMapper.toAuctionRequestResponseData(auctionRequest);
        return auctionRequestResponse;
    }

    public AuctionRequestResponse cancelAuctionRequest(int auctionRequestId, int breederID){
        AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_EXISTED));

        User breeder = userRepository.findById(breederID)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!breeder.getRole().equals(Role.BREEDER)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (auctionRequest.getRequestStatus().equals(AuctionRequestStatus.WAIT) && auctionRequest.getFish().getStatus().equals(KoiStatus.NEW)) {
            auctionRequest.setRequestStatus(AuctionRequestStatus.CANCEL);
            auctionRequest.getFish().setStatus(KoiStatus.CANCELED);

            auctionRequestRepository.save(auctionRequest);
        } else {
            throw new AppException(ErrorCode.CANT_CANCEL_REQUEST);
        }

        return AuctionRequestResponse.builder()
                .success(true)
                .message("Your request has been cancelled successfully.")
                .build();
    }

    public AuctionRequestResponse approveAuctionRequestForStaff(int auctionRequestId, int staffId) {

        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!staff.getRole().equals(Role.STAFF)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_EXISTED));


        if(auctionRequest.getRequestStatus().equals(AuctionRequestStatus.WAIT) && auctionRequest.getFish().getStatus().equals(KoiStatus.PENDING_APPROVAL)){

            auctionRequest.setRequestStatus(AuctionRequestStatus.APPROVE);
            auctionRequest.getFish().setStatus(KoiStatus.APPROVED);

            auctionRequestRepository.save(auctionRequest);
        } else {
            throw new AppException(ErrorCode.INVALID_AUCTION_REQUEST_AND_FISH_STATE);
        }



        return AuctionRequestResponse.builder()
                .success(true)
                .message("Request has been approved successfully.")
                .data(auctionRequest)
                .build();
    }

    public AuctionRequestResponse rejectAuctionRequestForStaff(int auctionRequestId, int staffId) {

        User staff = userRepository.findById(staffId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!staff.getRole().equals(Role.STAFF)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_EXISTED));

        if (auctionRequest.getRequestStatus().equals(AuctionRequestStatus.WAIT)
                && auctionRequest.getFish().getStatus().equals(KoiStatus.PENDING_APPROVAL)) {

            auctionRequest.setRequestStatus(AuctionRequestStatus.CANCEL);
            auctionRequest.getFish().setStatus(KoiStatus.REJECTED);

            auctionRequestRepository.save(auctionRequest);
        } else {
            throw new AppException(ErrorCode.INVALID_AUCTION_REQUEST_AND_FISH_STATE);
        }

        return AuctionRequestResponse
                .builder()
                .success(true)
                .message("Request has been rejected successfully.")
                .build();

    }

    private void checkRequest(AuctionRequestDTO auctionRequestDTO, User user, KoiFish fish) {

        if(!user.getRole().equals(Role.BREEDER)){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }


        if(!fish.getStatus().equals(KoiStatus.NEW)){
            throw new AppException(ErrorCode.FISH_NOT_AVAILABLE);
        }

        if(auctionRequestDTO.getStartPrice() <= 0){
            throw new AppException(ErrorCode.INVALID_START_PRICE);
        }

        if(auctionRequestDTO.getIncrementPrice() <= 0){
            throw new AppException(ErrorCode.INVALID_INCREMENT_PRICE);
        }

        Instant start_time = auctionRequestDTO.getStart_time();
        Instant end_time = auctionRequestDTO.getEnd_time();
        Instant now = Instant.now();

        if(start_time.isAfter(end_time)){
            throw new AppException(ErrorCode.INVALID_START_TIME);
        }

        Duration duration = Duration.between(start_time, now);

        if(duration.toDays() < 1){
            throw new AppException(ErrorCode.START_TIME_TOO_CLOSED);
        }
    }

    private AuctionRequest saveRequest(AuctionRequestDTO auctionRequestDTO, User user, KoiFish fish) {
        fish.setStatus(KoiStatus.PENDING_APPROVAL);
        AuctionRequest auctionRequest = AuctionRequest.builder()
                .breeder(user)
                .fish(fish)
                .startPrice(auctionRequestDTO.getStartPrice())
                .incrementPrice(auctionRequestDTO.getIncrementPrice())
                .methodType(auctionRequestDTO.getMethodType())
                .startTime(auctionRequestDTO.getStart_time())
                .endTime(auctionRequestDTO.getEnd_time())
                .requestStatus(AuctionRequestStatus.WAIT)
                .build();

        auctionRequestRepository.save(auctionRequest);

        return auctionRequest;
    }

    private AuctionRequest updateRequest(AuctionRequest auctionRequest, AuctionRequestDTO auctionRequestDTO, User user, KoiFish fish) {
        auctionRequest = auctionRequest.toBuilder()
                .fish(fish)
                .startPrice(auctionRequestDTO.getStartPrice())
                .incrementPrice(auctionRequestDTO.getIncrementPrice())
                .methodType(auctionRequestDTO.getMethodType())
                .startTime(auctionRequestDTO.getStart_time())
                .endTime(auctionRequestDTO.getEnd_time())
                .build();

        auctionRequestRepository.save(auctionRequest);

        return auctionRequest;
    }
}
