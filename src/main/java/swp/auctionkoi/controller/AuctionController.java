package swp.auctionkoi.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.request.*;
import swp.auctionkoi.dto.respone.ApiResponse;

import swp.auctionkoi.dto.respone.AuctionRequestUpdateResponse;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.Auction;
import swp.auctionkoi.dto.respone.auction.AuctionHistoryResponse;
import swp.auctionkoi.models.AuctionRequest;
import swp.auctionkoi.models.Bid;
import swp.auctionkoi.models.User;
import swp.auctionkoi.models.enums.Role;
import swp.auctionkoi.repository.BidRepository;
import swp.auctionkoi.repository.UserRepository;
import swp.auctionkoi.service.auction.AuctionService;
import swp.auctionkoi.service.auctionrequest.AuctionRequestService;
import swp.auctionkoi.service.koifish.impl.KoiFishServiceImpl;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/auctions")
@CrossOrigin("*")
public class AuctionController {

    @Autowired
    private AuctionRequestService auctionRequestService;

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private KoiFishServiceImpl koiFishService;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private UserRepository userRepository;

    private User getUserFromContext(){
        var context = SecurityContextHolder.getContext();

        String username = context.getAuthentication().getName();

        return userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    @GetMapping("/past-auction")
    public ApiResponse<List<AuctionHistoryResponse>> getAuctionComplete() {
        return ApiResponse.<List<AuctionHistoryResponse>>builder()
                .code(200)
                .message("OK")
                .result(auctionService.getListAuctionComplete())
                .build();
    }


    @GetMapping("/view-all-requests")
    public ApiResponse<List<AuctionRequest>> viewAllAuctionRequest() {
        List<AuctionRequest> auctionRequestResponseHashMap = auctionRequestService.viewAllAuctionRequest();
        return ApiResponse.<List<AuctionRequest>>builder()
                .code(200)
                .result(auctionRequestResponseHashMap)
                .build();
    }

    @GetMapping("/view-request-detail/{auctionRequestId}")
    public ApiResponse<AuctionRequestResponseData> viewAuctionRequestDetail(@PathVariable Integer auctionRequestId) {
        AuctionRequestResponseData auctionRequestResponseData = auctionRequestService.viewAuctionRequestDetail(auctionRequestId);

        return ApiResponse.<AuctionRequestResponseData>builder()
                .code(200)
                .result(auctionRequestResponseData)
                .build();
    }

    @GetMapping("/manager-review")
    public ApiResponse<List<AuctionRequest>> getAuctionRequestsInManagerReview() {
        List<AuctionRequest> auctionRequests = auctionRequestService.getAuctionRequestsInManagerReview();
        log.info("Check size auctionRequestResponseDataHashMap: {}", auctionRequests.size());
        return ApiResponse.<List<AuctionRequest>>builder()
                .code(200)
                .message("Successfully")
                .result(auctionRequests)
                .build();
    }

    @GetMapping("/wait")
    public ApiResponse<List<AuctionRequest>> getAuctionRequestsInWait() {
        List<AuctionRequest> auctionRequests = auctionRequestService.getAuctionRequestsInWait();
        return ApiResponse.<List<AuctionRequest>>builder()
                .code(200)
                .message("Successfully")
                .result(auctionRequests)
                .build();
    }

    @GetMapping("/awaiting_schedule")
    public ApiResponse<List<AuctionRequest>> getAllAuctionRequestForManage() {
        List<AuctionRequest> auctionRequests = auctionRequestService.getAuctionRequestsInAwaitingSchedule();
        return ApiResponse.<List<AuctionRequest>>builder()
                .code(200)
                .message("Successfully")
                .result(auctionRequests)
                .build();
    }

    @GetMapping("/assigned-staff")
    public ApiResponse<List<AuctionRequest>> getAuctionRequestsInAssignedStaff() {

        User staff = getUserFromContext();

        if(!staff.getRole().equals(Role.STAFF)){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        List<AuctionRequest> auctionRequests = auctionRequestService.getAuctionRequestsInAssignedToStaff(staff.getId());
        return ApiResponse.<List<AuctionRequest>>builder()
                .code(200)
                .message("Successfully")
                .result(auctionRequests)
                .build();
    }

    @GetMapping("/awaiting-schedule")
    public ApiResponse<List<AuctionRequest>> getAllAuctinoRequestWaitForSchedule() {

        User staff = getUserFromContext();

        if(!staff.getRole().equals(Role.STAFF)){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        List<AuctionRequest> auctionRequests = auctionRequestService.getAuctionRequestsInAwaitingSchedule(staff.getId());
        return ApiResponse.<List<AuctionRequest>>builder()
                .code(200)
                .message("Successfully")
                .result(auctionRequests)
                .build();
    }

    @GetMapping("/staff/all-assigned")
    public ApiResponse<List<AuctionRequest>> getAuctionRequestsByAssignedStaff() {

        User staff = getUserFromContext();
        if(!staff.getRole().equals(Role.STAFF)){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        List<AuctionRequest> auctionRequests = auctionRequestService.getAuctionRequestsByAssignedToStaff(staff.getId());
        return ApiResponse.<List<AuctionRequest>>builder()
                .code(200)
                .message("Successfully")
                .result(auctionRequests)
                .build();
    }

    @PutMapping("/staff/send-to-manager/{auctionRequestId}")
    public ApiResponse<AuctionRequestUpdateResponse> sendToManager(@PathVariable int auctionRequestId) {

        User staff = getUserFromContext();
        if(!staff.getRole().equals(Role.STAFF)){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        AuctionRequestUpdateResponse response = auctionRequestService.SendToManager(
                auctionRequestId,
                staff.getId()
        );
        return ApiResponse.<AuctionRequestUpdateResponse>builder()
                .code(200)
                .message("Request sent to manager for review")
                .result(response)
                .build();
    }

    @PutMapping("/staff/approve/{auctionRequestId}")
    public ApiResponse<AuctionRequestUpdateResponse> approveByStaff(@PathVariable int auctionRequestId) {

        User staff = getUserFromContext();
        if(!staff.getRole().equals(Role.STAFF)){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        AuctionRequestUpdateResponse response = auctionRequestService.approveDirectlyByStaff(
                auctionRequestId,
                staff.getId()
        );
        return ApiResponse.<AuctionRequestUpdateResponse>builder()
                .code(200)
                .message("Request approved by staff")
                .result(response)
                .build();
    }


    @PutMapping("/manager/reject/{auctionRequestId}")
    public ApiResponse<AuctionRequestUpdateResponse> rejectByManager(@PathVariable int auctionRequestId) {

        User manager = getUserFromContext();

        if(!manager.getRole().equals(Role.MANAGER)){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        AuctionRequestUpdateResponse response = auctionRequestService.rejectByManager(
                auctionRequestId,
                manager.getId()
        );
        return ApiResponse.<AuctionRequestUpdateResponse>builder()
                .code(200)
                .message("Request rejected by manager")
                .result(response)
                .build();
    }

    @PutMapping("/manager/assign-staff")
    public ApiResponse<AuctionRequestUpdateResponse> assignToStaff(@RequestBody ManagerActionDto request) {

        User manager = getUserFromContext();
        if(!manager.getRole().equals(Role.MANAGER)){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        AuctionRequestUpdateResponse response = auctionRequestService.assignToStaffByManager(
                request.getAuctionRequestId(),
                manager.getId(),
                request.getStaffId()
        );
        return ApiResponse.<AuctionRequestUpdateResponse>builder()
                .code(200)
                .message("Request assigned to staff")
                .result(response)
                .build();
    }

    @PutMapping("/assignedstaff/approve/{auctionRequestId}")
    public ApiResponse<AuctionRequestUpdateResponse> approveByAssignedStaff(@PathVariable Integer auctionRequestId) {

        User staff = getUserFromContext();
        if(!staff.getRole().equals(Role.STAFF)){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        AuctionRequestUpdateResponse response = auctionRequestService.rejectByAssignedStaff(
                auctionRequestId,
                staff.getId()
        );
        return ApiResponse.<AuctionRequestUpdateResponse>builder()
                .code(200)
                .message("Request approved by assigned staff")
                .result(response)
                .build();
    }

    @PutMapping("/assignedstaff/reject/{auctionRequestId}")
    public ApiResponse<AuctionRequestUpdateResponse> rejectByAssignedStaff(@PathVariable Integer auctionRequestId) {

        User staff = getUserFromContext();
        if(!staff.getRole().equals(Role.STAFF)){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        AuctionRequestUpdateResponse response = auctionRequestService.rejectByAssignedStaff(
                auctionRequestId,
                staff.getId()
        );
        return ApiResponse.<AuctionRequestUpdateResponse>builder()
                .code(200)
                .message("Request rejected by assigned staff")
                .result(response)
                .build();
    }

    @PutMapping("/schedule")
    public ApiResponse<AuctionRequestUpdateResponse> scheduleAuction(@RequestBody @Valid ScheduleAuctionRequestDTO scheduleRequest) {

        User staff = getUserFromContext();

        if(!staff.getRole().equals(Role.STAFF)){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        AuctionRequestUpdateResponse response = auctionRequestService.scheduleAuction(
                scheduleRequest.getAuctionRequestId(),
                staff.getId(),
                scheduleRequest.getIncrementStep(),
                scheduleRequest.getStartTime(),
                scheduleRequest.getEndTime()
        );
        return ApiResponse.<AuctionRequestUpdateResponse>builder()
                .code(200)
                .message("Request scheduled update successfully")
                .result(response)
                .build();
    }


    @GetMapping("/view-past-auction")
    public ApiResponse<List<AuctionHistoryResponse>> viewPastAuction() {
        return ApiResponse.<List<AuctionHistoryResponse>>builder()
                .code(200)
                .message("Successfully")
                .result(auctionService.getListAuctionComplete())
                .build();
    }


    @GetMapping("/detail/{id}")
    public ApiResponse<Auction> getAuctionById(@PathVariable int id) {
        Auction auction = auctionService.getAuctionById(id);
        return ApiResponse.<Auction>builder()
                .code(200)
                .message("Successfully")
                .result(auction)
                .build();
    }

    @GetMapping("/listBidOfUser/{auctionId}/{userId}")
    public ApiResponse<List<Bid>> listBidOfUser(@PathVariable int auctionId, @PathVariable int userId) {
        List<Bid> bidOfUser = bidRepository.findListBidByAuctionIdAndUserId(auctionId, userId);

        return ApiResponse.<List<Bid>>builder()
                .code(200)
                .result(bidOfUser)
                .build();
    }


//    @PutMapping("/reject/{auctionRequestId}")
//    public AuctionRequestResponse rejectAuctionRequestForStaff(@PathVariable Integer auctionRequestId, @RequestHeader("staffId") Integer staffId) {
//        return auctionRequestService.rejectAuctionRequestForStaff(auctionRequestId, staffId);
//    }
}