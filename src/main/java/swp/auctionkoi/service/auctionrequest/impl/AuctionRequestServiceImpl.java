package swp.auctionkoi.service.auctionrequest.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.request.AuctionRequestDTO;
import swp.auctionkoi.dto.request.AuctionRequestResponseData;
import swp.auctionkoi.dto.request.KoiFishDTO;
import swp.auctionkoi.dto.request.UserDTO;
import swp.auctionkoi.dto.respone.AuctionRequestResponse;
import swp.auctionkoi.dto.respone.AuctionRequestUpdateResponse;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.mapper.AuctionRequestMapper;
import swp.auctionkoi.models.*;
import swp.auctionkoi.models.enums.AuctionRequestStatus;
import swp.auctionkoi.models.enums.AuctionStatus;
import swp.auctionkoi.models.enums.Role;
import swp.auctionkoi.repository.*;
import swp.auctionkoi.service.auctionrequest.AuctionRequestService;
import swp.auctionkoi.models.enums.KoiStatus;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
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

    public AuctionRequest getAuctionRequestDetailByAuctionId(int auctionId){
        return auctionRequestRepository.findByAuctionId(auctionId).get();
    }


    public AuctionRequest sendAuctionRequest(AuctionRequestDTO auctionRequestDTO) {
        // Fetching User and Fish
        User user = userRepository.findById(auctionRequestDTO.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        KoiFish fish = koiFishRepository.findById(auctionRequestDTO.getFishId())
                .orElseThrow(() -> new AppException(ErrorCode.FISH_NOT_EXISTED));

        if (fish.getStatus().equals(KoiStatus.PENDING_APPROVAL)) {
            throw new AppException(ErrorCode.INVALID_FISH_STATE);
        }
        checkRequest(auctionRequestDTO, user, fish);

        // Set fish status to PENDING_APPROVAL
        fish.setStatus(KoiStatus.PENDING_APPROVAL);

        // Creating and saving the auction request
        AuctionRequest auctionRequest = AuctionRequest.builder()
                .user(user)
                .fish(fish)
                .buyOut(auctionRequestDTO.getBuyOut())
//                .incrementStep(auctionRequestDTO.getIncrementStep())
                .startPrice(auctionRequestDTO.getStartPrice())
                .methodType(auctionRequestDTO.getMethodType())
                .requestStatus(AuctionRequestStatus.WAIT)
                .build();

        // Save the auction request
        auctionRequest = auctionRequestRepository.save(auctionRequest);

        // Return the saved auction request
        return auctionRequest;
    }


    public AuctionRequest updateAuctionRequestForBreeder(Integer auctionRequestId, AuctionRequestDTO auctionRequestDTO) {

        // Fetch existing AuctionRequest
        AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_EXISTED));

        // Check if the auction request is in a valid state to update
        if (auctionRequest.getAuction() != null && !auctionRequest.getRequestStatus().equals(AuctionRequestStatus.WAIT)) {
            throw new AppException(ErrorCode.INVALID_AUCTION_REQUEST_STATE);
        }

        // Fetch the user
        User user = userRepository.findById(auctionRequestDTO.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Determine which KoiFish to use for the update
        KoiFish fish;
        if (auctionRequestDTO.getFishId() != null) {
            fish = koiFishRepository.findById(auctionRequestDTO.getFishId())
                    .orElseThrow(() -> new AppException(ErrorCode.FISH_NOT_EXISTED));
            if (!fish.getBreeder().equals(user)) {
                throw new AppException(ErrorCode.NOT_BELONG_TO_BREEDER);
            }
        } else {
            fish = auctionRequest.getFish();
        }

        // Validate request data
        checkRequest(auctionRequestDTO, user, fish);

        // Update the AuctionRequest fields
        auctionRequest.setUser(user);
        auctionRequest.setFish(fish);
        auctionRequest.setBuyOut(auctionRequestDTO.getBuyOut());
//        auctionRequest.setIncrementStep(auctionRequestDTO.getIncrementStep());
        auctionRequest.setStartPrice(auctionRequestDTO.getStartPrice());
        auctionRequest.setMethodType(auctionRequestDTO.getMethodType());
//        auctionRequest.setStartTime(auctionRequestDTO.getStart_time());
//        auctionRequest.setEndTime(auctionRequestDTO.getEnd_time());
        auctionRequest.setRequestUpdatedDate(Instant.now());

        // Save the updated AuctionRequest
        auctionRequest = auctionRequestRepository.save(auctionRequest);

        // Return the updated AuctionRequest
        return auctionRequest;
    }


    @Override
    public List<AuctionRequest> viewAllAuctionRequest() {
        List<AuctionRequest> auctionRequests = new ArrayList<>();
        List<AuctionRequest> auctionRequestList = auctionRequestRepository.findAll();
        for (AuctionRequest auctionRequest : auctionRequestList) {
            auctionRequests.add(auctionRequest);
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
    public List<AuctionRequest> viewAllAuctionRequestsForBreeder(Integer userId) {
        List<AuctionRequest> result = new ArrayList<>();
        List<AuctionRequest> auctionRequestList = auctionRequestRepository.findListAuctionRequestByUserId(userId);

        for (AuctionRequest auctionRequest : auctionRequestList) {
            result.add(auctionRequest);
        }
        return result;
    }

    public AuctionRequestResponse cancelAuctionRequest(int auctionRequestId, int breederID) {
        AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_EXISTED));

        User breeder = userRepository.findById(breederID)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!breeder.getRole().equals(Role.BREEDER)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (!auctionRequest.getUser().getId().equals(breederID)) {
            throw new AppException(ErrorCode.AUCTION_REQUEST_NOT_FOUND);
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

    @Override
    public List<AuctionRequest> getAuctionRequestsInManagerReview() {
        return auctionRequestRepository.findByRequestStatus(AuctionRequestStatus.MANAGER_REVIEW);
    }

    @Override
    public List<AuctionRequest> getAuctionRequestsInWait() {
        return auctionRequestRepository.findByRequestStatus(AuctionRequestStatus.WAIT);
    }

    @Override
    public List<AuctionRequest> getAuctionRequestsInAssignedToStaff(Integer staffId) {
        return auctionRequestRepository.findByAssignedStaffIdAndStatus(
                staffId,
                AuctionRequestStatus.ASSIGNED_TO_STAFF
        );
    }

    @Override
    public List<AuctionRequest> getAuctionRequestsInAwaitingSchedule(Integer staffId) {
        return auctionRequestRepository.findByAssignedStaffIdAndStatus(
                staffId,
                AuctionRequestStatus.AWAITING_SCHEDULE
        );
    }

    @Override
    public List<AuctionRequest> getAuctionRequestsByAssignedToStaff(Integer staffId) {
        return auctionRequestRepository.findByAssignedStaffId(staffId);
    }

    @Override
    public List<AuctionRequest> getAuctionRequestsInAwaitingSchedule() {
        return auctionRequestRepository.findByRequestStatus(AuctionRequestStatus.AWAITING_SCHEDULE);
    }

    // For Staff initial review
    @Override
    public AuctionRequestUpdateResponse SendToManager(int auctionRequestId, int staffId) {
        User staff = validateStaff(staffId);
        AuctionRequest auctionRequest = validateInitialState(auctionRequestId);

        auctionRequest.setRequestStatus(AuctionRequestStatus.MANAGER_REVIEW);
        auctionRequest.getFish().setStatus(KoiStatus.PENDING_APPROVAL);

        auctionRequestRepository.save(auctionRequest);
        return createUpdateResponse(auctionRequest);
    }

    @Override
    public AuctionRequestUpdateResponse approveDirectlyByStaff(int auctionRequestId, int staffId) {
        User staff = validateStaff(staffId);
        AuctionRequest auctionRequest = validateInitialState(auctionRequestId);

        auctionRequest.setRequestStatus(AuctionRequestStatus.AWAITING_SCHEDULE);
        auctionRequest.getFish().setStatus(KoiStatus.APPROVED);
        auctionRequest.setAssignedStaff(staff);

        // Create new auction
        Auction newAuction = createNewAuction(auctionRequest.getFish());
        auctionRequest.setAuction(newAuction);

        auctionRequestRepository.save(auctionRequest);
        return createUpdateResponse(auctionRequest);
    }

//    // For Manager review
//    @Override
//    public AuctionRequestUpdateResponse approveByManager(int auctionRequestId, int managerId, Integer staffId) {
//        User manager = validateManager(managerId);
//        AuctionRequest auctionRequest = validateManagerReviewState(auctionRequestId);
//
//        auctionRequest.setRequestStatus(AuctionRequestStatus.APPROVE);
//        auctionRequest.getFish().setStatus(KoiStatus.APPROVED);
//
//        if (staffId != null) {
//            User staff = validateStaff(staffId);
//            auctionRequest.setAssignedStaff(staff);
//        }
//
//        // Create new auction
//        Auction newAuction = createNewAuction(auctionRequest.getFish());
//        auctionRequest.setAuction(newAuction);
//
//        auctionRequestRepository.save(auctionRequest);
//        return createUpdateResponse(auctionRequest);
//    }

    @Override
    public AuctionRequestUpdateResponse rejectByManager(int auctionRequestId, int managerId) {
        User manager = validateManager(managerId);
        AuctionRequest auctionRequest = validateManagerReviewState(auctionRequestId);

        auctionRequest.setRequestStatus(AuctionRequestStatus.REJECTED);
        auctionRequest.getFish().setStatus(KoiStatus.REJECTED);

        auctionRequestRepository.save(auctionRequest);
        return createUpdateResponse(auctionRequest);
    }


    @Override
    public AuctionRequestUpdateResponse assignToStaffByManager(int auctionRequestId, int managerId, Integer staffId) {
        if (staffId == null) {
            throw new AppException(ErrorCode.STAFF_ID_REQUIRED);
        }

        User manager = validateManager(managerId);
        User staff = validateStaff(staffId);
        AuctionRequest auctionRequest = validateManagerReviewState(auctionRequestId);

        auctionRequest.setRequestStatus(AuctionRequestStatus.ASSIGNED_TO_STAFF);
        auctionRequest.getFish().setStatus(KoiStatus.PENDING_APPROVAL);
        auctionRequest.setAssignedStaff(staff);

        auctionRequestRepository.save(auctionRequest);
        return createUpdateResponse(auctionRequest);
    }

    // For Staff after manager assignment
    @Override
    public AuctionRequestUpdateResponse approveByAssignedStaff(int auctionRequestId, int staffId) {
        User staff = validateStaff(staffId);
        AuctionRequest auctionRequest = validateStaffAssignment(auctionRequestId, staffId);

        auctionRequest.setRequestStatus(AuctionRequestStatus.AWAITING_SCHEDULE);
        auctionRequest.getFish().setStatus(KoiStatus.APPROVED);

        // Create new auction
        Auction newAuction = createNewAuction(auctionRequest.getFish());
        auctionRequest.setAuction(newAuction);

        auctionRequestRepository.save(auctionRequest);
        return createUpdateResponse(auctionRequest);
    }

    @Override
    public AuctionRequestUpdateResponse rejectByAssignedStaff(int auctionRequestId, int staffId) {
        User staff = validateStaff(staffId);
        AuctionRequest auctionRequest = validateStaffAssignment(auctionRequestId, staffId);

        auctionRequest.setRequestStatus(AuctionRequestStatus.REJECTED);
        auctionRequest.getFish().setStatus(KoiStatus.REJECTED);

        auctionRequestRepository.save(auctionRequest);
        return createUpdateResponse(auctionRequest);
    }


    // Add this new method
    @Override
    public AuctionRequestUpdateResponse scheduleAuction(int auctionRequestId, int staffId,int incrementStep, Instant startTime, Instant endTime) {
        User staff = validateStaff(staffId);
        AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_EXISTED));

        // Validate auction request state
        if (!auctionRequest.getRequestStatus().equals(AuctionRequestStatus.AWAITING_SCHEDULE)) {
            throw new AppException(ErrorCode.INVALID_AUCTION_REQUEST_STATE);
        }

        if (!auctionRequest.getAssignedStaff().getId().equals(staffId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Instant now = Instant.now().plus(Duration.ofHours(7));


        Instant adjustedStartTime = startTime.plus(Duration.ofHours(7));
        Instant adjustedEndTime = endTime.plus(Duration.ofHours(7));

        // Validate times
        if (adjustedStartTime.isBefore(now) || adjustedEndTime.isBefore(adjustedStartTime)) {
            throw new AppException(ErrorCode.INVALID_AUCTION_TIME);
        }

        // Check minimum duration of 10 minutes
        long durationMinutes = java.time.Duration.between(adjustedStartTime, adjustedEndTime).toMinutes();
        if (durationMinutes < 10) {
            throw new AppException(ErrorCode.INVALID_AUCTION_DURATION);
        }

        Instant startTimeMinusOneHour = adjustedStartTime.minus(Duration.ofHours(1));
        List<AuctionRequest> closeEndTimeRequests = auctionRequestRepository.findCloseEndTimes(startTimeMinusOneHour, adjustedStartTime);
        if (!closeEndTimeRequests.isEmpty()) {
            throw new AppException(ErrorCode.AUCTION_TOO_CLOSE_TO_PREVIOUS);
        }

        // Update auction request
        auctionRequest.setIncrementStep(incrementStep);
        auctionRequest.setStartTime(adjustedStartTime);
        auctionRequest.setEndTime(adjustedEndTime);
        auctionRequest.setRequestStatus(AuctionRequestStatus.SCHEDULED);

        // Update associated auction
        Auction auction = auctionRequest.getAuction();
        if (auction != null) {
            auction.setStatus(AuctionStatus.PENDING);
            auctionRepository.save(auction);
        }

        auctionRequestRepository.save(auctionRequest);
        return createUpdateResponse(auctionRequest);
    }

    // Helper methods
    private User validateStaff(int staffId) {
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (!staff.getRole().equals(Role.STAFF)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return staff;
    }

    private User validateManager(int managerId) {
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (!manager.getRole().equals(Role.MANAGER)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return manager;
    }

    private AuctionRequest validateInitialState(int auctionRequestId) {
        AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_EXISTED));
        if (!auctionRequest.getRequestStatus().equals(AuctionRequestStatus.WAIT) ||
                !auctionRequest.getFish().getStatus().equals(KoiStatus.PENDING_APPROVAL)) {
            throw new AppException(ErrorCode.INVALID_AUCTION_REQUEST_AND_FISH_STATE);
        }
        return auctionRequest;
    }

    private AuctionRequest validateManagerReviewState(int auctionRequestId) {
        AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_EXISTED));
        if (!auctionRequest.getRequestStatus().equals(AuctionRequestStatus.MANAGER_REVIEW)) {
            throw new AppException(ErrorCode.INVALID_AUCTION_REQUEST_STATE);
        }
        return auctionRequest;
    }

    private AuctionRequest validateStaffAssignment(int auctionRequestId, int staffId) {
        AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_EXISTED));
        if (!auctionRequest.getRequestStatus().equals(AuctionRequestStatus.ASSIGNED_TO_STAFF)) {
            throw new AppException(ErrorCode.INVALID_AUCTION_REQUEST_STATE);
        }
        if (!auctionRequest.getAssignedStaff().getId().equals(staffId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return auctionRequest;
    }

    private Auction createNewAuction(KoiFish fish) {
        Auction newAuction = new Auction();
        newAuction.setFish(fish);
        newAuction.setStatus(AuctionStatus.NEW);
        return auctionRepository.save(newAuction);
    }

    private AuctionRequestUpdateResponse createUpdateResponse(AuctionRequest auctionRequest) {
        return new AuctionRequestUpdateResponse(
                auctionRequest.getId(),
                UserDTO.fromUser(auctionRequest.getUser()),
                auctionRequest.getAssignedStaff() != null ? auctionRequest.getAssignedStaff().getId() : null,
                auctionRequest.getRequestStatus().name(),
                KoiFishDTO.fromKoiFish(auctionRequest.getFish()),
                auctionRequest.getBuyOut(),
                auctionRequest.getIncrementStep(),
                auctionRequest.getStartPrice(),
                auctionRequest.getMethodType(),
                auctionRequest.getRequestCreatedDate(),
                auctionRequest.getRequestUpdatedDate(),
                auctionRequest.getStartTime(),
                auctionRequest.getEndTime()
        );
    }


    private void checkRequest(AuctionRequestDTO auctionRequestDTO, User user, KoiFish fish) {

        if (!user.getRole().equals(Role.BREEDER)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (!fish.getStatus().equals(KoiStatus.NEW) && !fish.getStatus().equals(KoiStatus.PENDING_APPROVAL)) {
            throw new AppException(ErrorCode.FISH_NOT_AVAILABLE);
        }

        if (!fish.getBreeder().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.NOT_BELONG_TO_BREEDER);
        }

        if (auctionRequestDTO.getStartPrice() <= 0) {
            throw new AppException(ErrorCode.INVALID_PRICE);
        }

        if(auctionRequestDTO.getBuyOut() <= 0){
            throw new AppException(ErrorCode.INVALID_PRICE);
        }

//        Instant start_time = auctionRequestDTO.getStart_time();
//        Instant end_time = auctionRequestDTO.getEnd_time();
//        Instant now = Instant.now();

//        if (start_time.isAfter(end_time)) {
//            throw new AppException(ErrorCode.INVALID_START_TIME);
//        }

//        Duration duration = Duration.between(start_time, now);
//
//        if (duration.toDays() < 1) {
//            throw new AppException(ErrorCode.START_TIME_TOO_CLOSED);
//        }
    }
}




