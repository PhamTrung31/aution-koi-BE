package swp.auctionkoi.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.request.*;
import swp.auctionkoi.dto.respone.ApiResponse;

import swp.auctionkoi.dto.respone.AuctionRequestUpdateResponse;
import swp.auctionkoi.models.Auction;
import swp.auctionkoi.dto.respone.auction.AuctionHistoryResponse;
import swp.auctionkoi.models.AuctionRequest;
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

    @GetMapping("/past-auction")
    public ApiResponse<List<AuctionHistoryResponse>> getAuctionComplete(){
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
    public ApiResponse<List<AuctionRequest>> getAuctionRequestsInAwaitingSchedule() {
        List<AuctionRequest> auctionRequests = auctionRequestService.getAuctionRequestsInAwaitingSchedule();
        return ApiResponse.<List<AuctionRequest>>builder()
                .code(200)
                .message("Successfully")
                .result(auctionRequests)
                .build();
    }

    @GetMapping("/assigned-staff/{staffId}")
    public ApiResponse<List<AuctionRequest>> getAuctionRequestsInAssignedStaff(@PathVariable Integer staffId) {
        List<AuctionRequest> auctionRequests = auctionRequestService.getAuctionRequestsInAssignedToStaff(staffId);
        return ApiResponse.<List<AuctionRequest>>builder()
                .code(200)
                .message("Successfully")
                .result(auctionRequests)
                .build();
    }

    @GetMapping("/staff/all-assigned/{staffId}")
    public ApiResponse<List<AuctionRequest>> getAuctionRequestsByAssignedStaff(@PathVariable Integer staffId) {
        List<AuctionRequest> auctionRequests = auctionRequestService.getAuctionRequestsByAssignedToStaff(staffId);
        return ApiResponse.<List<AuctionRequest>>builder()
                .code(200)
                .message("Successfully")
                .result(auctionRequests)
                .build();
    }

    @PutMapping("/staff/send-to-manager")
    public ApiResponse<AuctionRequestUpdateResponse> sendToManager(@RequestBody AuctionRequestActionDto request) {
        AuctionRequestUpdateResponse response = auctionRequestService.SendToManager(
                request.getAuctionRequestId(),
                request.getStaffId()
        );
        return ApiResponse.<AuctionRequestUpdateResponse>builder()
                .code(200)
                .message("Request sent to manager for review")
                .result(response)
                .build();
    }

    @PutMapping("/staff/approve")
    public ApiResponse<AuctionRequestUpdateResponse> approveByStaff(@RequestBody AuctionRequestActionDto request) {
        AuctionRequestUpdateResponse response = auctionRequestService.approveDirectlyByStaff(
                request.getAuctionRequestId(),
                request.getStaffId()
        );
        return ApiResponse.<AuctionRequestUpdateResponse>builder()
                .code(200)
                .message("Request approved by staff")
                .result(response)
                .build();
    }


    @PutMapping("/manager/reject")
    public ApiResponse<AuctionRequestUpdateResponse> rejectByManager(@RequestBody ManagerRejectDto request) {
        AuctionRequestUpdateResponse response = auctionRequestService.rejectByManager(
                request.getAuctionRequestId(),
                request.getManagerId()
        );
        return ApiResponse.<AuctionRequestUpdateResponse>builder()
                .code(200)
                .message("Request rejected by manager")
                .result(response)
                .build();
    }

    @PutMapping("/manager/assign-staff")
    public ApiResponse<AuctionRequestUpdateResponse> assignToStaff(@RequestBody ManagerActionDto request) {
        AuctionRequestUpdateResponse response = auctionRequestService.assignToStaffByManager(
                request.getAuctionRequestId(),
                request.getManagerId(),
                request.getStaffId()
        );
        return ApiResponse.<AuctionRequestUpdateResponse>builder()
                .code(200)
                .message("Request assigned to staff")
                .result(response)
                .build();
    }

    @PutMapping("/assignedstaff/approve")
    public ApiResponse<AuctionRequestUpdateResponse> approveByAssignedStaff(@RequestBody AuctionRequestActionDto request) {
        AuctionRequestUpdateResponse response = auctionRequestService.approveByAssignedStaff(
                request.getAuctionRequestId(),
                request.getStaffId()
        );
        return ApiResponse.<AuctionRequestUpdateResponse>builder()
                .code(200)
                .message("Request approved by assigned staff")
                .result(response)
                .build();
    }

    @PutMapping("/assignedstaff/reject")
    public ApiResponse<AuctionRequestUpdateResponse> rejectByAssignedStaff(@RequestBody AuctionRequestActionDto request) {
        AuctionRequestUpdateResponse response = auctionRequestService.rejectByAssignedStaff(
                request.getAuctionRequestId(),
                request.getStaffId()
        );
        return ApiResponse.<AuctionRequestUpdateResponse>builder()
                .code(200)
                .message("Request rejected by assigned staff")
                .result(response)
                .build();
    }

    @PutMapping("/schedule")
    public ApiResponse<AuctionRequestUpdateResponse> scheduleAuction( @RequestBody ScheduleAuctionRequestDTO scheduleRequest
    ) {
        AuctionRequestUpdateResponse response = auctionRequestService.scheduleAuction(
                scheduleRequest.getAuctionRequestId(),
                scheduleRequest.getStaffId(),
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




//    @PutMapping("/reject/{auctionRequestId}")
//    public AuctionRequestResponse rejectAuctionRequestForStaff(@PathVariable Integer auctionRequestId, @RequestHeader("staffId") Integer staffId) {
//        return auctionRequestService.rejectAuctionRequestForStaff(auctionRequestId, staffId);
//    }
}