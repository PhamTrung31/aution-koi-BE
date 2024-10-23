package swp.auctionkoi.service.auctionrequest.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.request.AuctionRequestDTO;
import swp.auctionkoi.dto.request.AuctionRequestResponseData;
import swp.auctionkoi.dto.request.AuctionRequestUpdateDTO;
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
import java.util.HashMap;
import java.util.List;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class AuctionRequestServiceImpl implements AuctionRequestService {

    AuctionRequestRepository auctionRequestRepository;

    KoiFishRepository koiFishRepository;

    AuctionRepository auctionRepository;

    UserRepository userRepository;

    AuctionRequestMapper auctionRequestMapper;

    public AuctionRequestResponse sendAuctionRequest(AuctionRequestDTO auctionRequestDTO) {
        User user = userRepository.findById(auctionRequestDTO.getUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        KoiFish fish = koiFishRepository.findById(auctionRequestDTO.getFishId()).orElseThrow(() -> new AppException(ErrorCode.FISH_NOT_EXISTED));

        if (fish.getUser() != user) {
            throw new AppException(ErrorCode.NOT_BELONG_TO_BREEDER);
        }

        checkRequest(auctionRequestDTO, user, fish);

        AuctionRequest auctionRequest = saveRequest(auctionRequestDTO, user, fish);

        AuctionRequestResponse auctionRequestResponse = AuctionRequestResponse.builder()
                .success(true)
                .message("Your request has been sent successfully.")
                .data(auctionRequest)
                .build();

        return auctionRequestResponse;
    }

    public AuctionRequestResponse updateAuctionRequestForBreeder(Integer auctionRequestId, AuctionRequestDTO auctionRequestDTO) {

        AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId).orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_EXISTED));

        if (auctionRequest.getAuction() != null && !auctionRequest.getRequestStatus().equals(AuctionRequestStatus.WAIT)) {
            throw new AppException(ErrorCode.INVALID_AUCTION_REQUEST_STATE);
        }

        log.info(auctionRequest.getBuyOut().toString());

        User user = userRepository.findById(auctionRequestDTO.getUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        KoiFish fish;

        if (auctionRequestDTO.getFishId() != null) {
            fish = koiFishRepository.findById(auctionRequestDTO.getFishId()).orElseThrow(() -> new AppException(ErrorCode.FISH_NOT_EXISTED));
            if (fish.getUser() != user) {
                throw new AppException(ErrorCode.NOT_BELONG_TO_BREEDER);
            }

        } else {
            fish = auctionRequest.getFish();
        }

        checkFieldUpdate(auctionRequest, auctionRequestDTO);

        checkRequest(auctionRequestDTO, user, fish);

        AuctionRequest result = updateRequest(auctionRequest, auctionRequestDTO, fish);

        AuctionRequestResponse auctionRequestResponse = AuctionRequestResponse.builder()
                .success(true)
                .message("Your request has been update successfully.")
                .data(result)
                .build();
        return auctionRequestResponse;
    }


    @Override
    public HashMap<Integer, AuctionRequestResponseData> viewAllAuctionRequest() {
        HashMap<Integer, AuctionRequestResponseData> auctionRequests = new HashMap<>();
        List<AuctionRequest> auctionRequestList = auctionRequestRepository.findAll();
        for (AuctionRequest auctionRequest : auctionRequestList) {
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

    @Override
    public HashMap<Integer, AuctionRequestResponseData> viewAllAuctionRequestsForBreeder(Integer userId) {
        HashMap<Integer, AuctionRequestResponseData> auctionRequests = new HashMap<>();
        List<AuctionRequest> auctionRequestList = auctionRequestRepository.findListAuctionRequestByUserId(userId);
        for (AuctionRequest auctionRequest : auctionRequestList) {
            auctionRequests.put(auctionRequest.getId(), auctionRequestMapper.toAuctionRequestResponseData(auctionRequest));
            log.info(auctionRequest.getBuyOut().toString());
        }

        return auctionRequests;
    }

    public AuctionRequestResponse cancelAuctionRequest(int auctionRequestId, int breederID) {
        AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_EXISTED));

        User breeder = userRepository.findById(breederID)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!breeder.getRole().equals(Role.BREEDER)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (auctionRequest.getRequestStatus().equals(AuctionRequestStatus.WAIT) && auctionRequest.getFish().getStatus().equals(KoiStatus.PENDING_APPROVAL)) {
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


        if (auctionRequest.getRequestStatus().equals(AuctionRequestStatus.WAIT) && auctionRequest.getFish().getStatus().equals(KoiStatus.PENDING_APPROVAL)) {

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

        if (!user.getRole().equals(Role.BREEDER)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (!fish.getStatus().equals(KoiStatus.NEW) && !fish.getStatus().equals(KoiStatus.PENDING_APPROVAL)) {
            throw new AppException(ErrorCode.FISH_NOT_AVAILABLE);
        }

        if (auctionRequestDTO.getStartPrice() <= 0) {
            throw new AppException(ErrorCode.INVALID_START_PRICE);
        }

        Instant start_time = auctionRequestDTO.getStart_time();
        Instant end_time = auctionRequestDTO.getEnd_time();
        Instant now = Instant.now();

        if (start_time.isAfter(end_time)) {
            throw new AppException(ErrorCode.INVALID_START_TIME);
        }

//        Duration duration = Duration.between(start_time, now);
//
//        if(duration.toDays() < 1){
//            throw new AppException(ErrorCode.START_TIME_TOO_CLOSED);
//        }
    }

    private AuctionRequest saveRequest(AuctionRequestDTO auctionRequestDTO, User user, KoiFish fish) {
        fish.setStatus(KoiStatus.PENDING_APPROVAL);
        AuctionRequest auctionRequest = AuctionRequest.builder()
                .user(user)
                .fish(fish)
                .startPrice(auctionRequestDTO.getStartPrice())
                .buyOut(auctionRequestDTO.getBuyOut())
                .methodType(auctionRequestDTO.getMethodType())
                .startTime(auctionRequestDTO.getStart_time())
                .endTime(auctionRequestDTO.getEnd_time())
                .requestStatus(AuctionRequestStatus.WAIT)
                .build();

        auctionRequestRepository.save(auctionRequest);

        return auctionRequest;
    }

    private AuctionRequest updateRequest(AuctionRequest auctionRequest, AuctionRequestDTO auctionRequestDTO, KoiFish fish) {
        try{
            fish.setStatus(KoiStatus.PENDING_APPROVAL);
            auctionRequest.setFish(fish);
            auctionRequest.setStartPrice(auctionRequestDTO.getStartPrice());
            auctionRequest.setBuyOut(auctionRequestDTO.getBuyOut());
            auctionRequest.setMethodType(auctionRequestDTO.getMethodType());
            auctionRequest.setStartTime(auctionRequestDTO.getStart_time());
            auctionRequest.setEndTime(auctionRequestDTO.getEnd_time());

            auctionRequestRepository.save(auctionRequest);

            return auctionRequest;
        }catch(Exception e){
            throw new AppException(ErrorCode.ERROR_UPDATE); //temp error to check, delete after fix it
        }
    }

    //get field from original object
    private void checkFieldUpdate(AuctionRequest auctionRequest, AuctionRequestDTO auctionRequestDTO) {
        if (auctionRequestDTO.getBuyOut() == null) {
            auctionRequestDTO.setBuyOut(auctionRequest.getBuyOut());
        }
        if (auctionRequestDTO.getStartPrice() == null) {
            auctionRequestDTO.setStartPrice(auctionRequest.getStartPrice());
        }
        if (auctionRequestDTO.getStart_time() == null) {
            auctionRequestDTO.setStart_time(auctionRequest.getStartTime());
        }
        if (auctionRequestDTO.getEnd_time() == null) {
            auctionRequestDTO.setEnd_time(auctionRequest.getEndTime());
        }
        if (auctionRequestDTO.getMethodType() == null) {
            auctionRequestDTO.setMethodType(auctionRequest.getMethodType());
        }
    }
}
