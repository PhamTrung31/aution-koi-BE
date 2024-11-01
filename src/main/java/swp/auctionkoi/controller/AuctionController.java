package swp.auctionkoi.controller;

import com.google.protobuf.Api;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.request.*;
import swp.auctionkoi.dto.request.bid.BidRequest;
import swp.auctionkoi.dto.request.bid.BidRequestTraditional;
import swp.auctionkoi.dto.respone.ApiResponse;
import swp.auctionkoi.dto.respone.AuctionRequestResponse;
import swp.auctionkoi.dto.request.auction.AuctionJoinRequest;

import swp.auctionkoi.dto.respone.AuctionRequestUpdateResponse;
import swp.auctionkoi.dto.respone.auction.AuctionJoinResponse;
import swp.auctionkoi.dto.respone.auction.AuctionResonpse;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.Auction;
import swp.auctionkoi.models.AuctionRequest;
import swp.auctionkoi.models.enums.AuctionType;
import swp.auctionkoi.repository.AuctionRepository;
import swp.auctionkoi.repository.AuctionRequestRepository;
import swp.auctionkoi.service.auction.*;
import swp.auctionkoi.service.auctionrequest.AuctionRequestService;

import java.time.LocalDateTime;
import java.util.HashMap;
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

    @GetMapping("/past-auction")
    public ApiResponse<List<AuctionResonpse>> getAuctionComplete(){
        return ApiResponse.<List<AuctionResonpse>>builder()
                .code(200)
                .message("OK")
                .result(auctionService.getListAuctionComplete())
                .build();
    }

    @GetMapping("/view-all-requests")
    public ApiResponse<HashMap<Integer, AuctionRequestResponseData>> viewAllAuctionRequest() {
        HashMap<Integer, AuctionRequestResponseData> auctionRequestResponseHashMap = auctionRequestService.viewAllAuctionRequest();
        return ApiResponse.<HashMap<Integer, AuctionRequestResponseData>>builder()
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

    @PutMapping("/approve")
    public ApiResponse<AuctionRequestUpdateResponse> approveAuctionRequestForStaff(
            @RequestBody ApproveAuctionRequestDto approveRequestDto) {

        // Log giá trị nhận được từ frontend
//        System.out.println("Request received - auctionRequestId: " + approveRequestDto.getAuctionRequestId() +
//                ", staffId: " + approveRequestDto.getStaffId() +
//                ", isSendToManager: " + approveRequestDto.isSendToManager());

        // Gọi service để xử lý yêu cầu
        AuctionRequestUpdateResponse auctionRequestResponse = auctionRequestService.approveAuctionRequestForStaff(
                approveRequestDto.getAuctionRequestId(),
                approveRequestDto.getStaffId(),
                approveRequestDto.isSendToManager()
        );

        // Kiểm tra xem yêu cầu đã được gửi lên manager hay staff tự duyệt
        String message;
        if (approveRequestDto.isSendToManager()) {
            message = "Request sent to manager for approval";
        } else {
            message = "Successfully approved by staff";
        }

        // Trả về API response
        return ApiResponse.<AuctionRequestUpdateResponse>builder()
                .code(200)
                .message(message)
                .result(auctionRequestResponse)
                .build();
    }


    @PutMapping("/manager/review")
    public ApiResponse<AuctionRequestUpdateResponse> reviewAuctionRequestByManager(@RequestBody ManagerReviewRequest request) {
        AuctionRequestUpdateResponse auctionRequestResponse = auctionRequestService.reviewAuctionRequestByManager(
                request.getAuctionRequestId(),
                request.getManagerId(),
                request.isApproved(),
                request.isAssignToStaff());

        String message;
        if (request.isApproved()) {
            message = "The manager agreed to approve";
        } else if(request.isAssignToStaff()) {
            message = "Manager requests the staff who submitted the request to inspect the fish first";
        } else {
            message = "Request not approved by manager";
        }

        return ApiResponse.<AuctionRequestUpdateResponse>builder()
                .code(200)
                .message(message)
                .result(auctionRequestResponse)
                .build();
    }

    @PutMapping("/staff/review")
    public ApiResponse<AuctionRequestUpdateResponse> reviewAuctionRequestByStaff(@RequestBody StaffReviewRequest request) {
        AuctionRequestUpdateResponse auctionRequestResponse = auctionRequestService.reviewAuctionRequestByStaff(
                request.getAuctionRequestId(),
                request.getStaffId(),
                request.isApproved());

        String message;
        if (request.isApproved()) {
            message = "Successfully approved by staff";
        } else {
            message = "Request not approved by staff";
        }

        return ApiResponse.<AuctionRequestUpdateResponse>builder()
                .code(200)
                .message(message)
                .result(auctionRequestResponse)
                .build();
    }

//    @PutMapping("/reject/{auctionRequestId}")
//    public AuctionRequestResponse rejectAuctionRequestForStaff(@PathVariable Integer auctionRequestId, @RequestHeader("staffId") Integer staffId) {
//        return auctionRequestService.rejectAuctionRequestForStaff(auctionRequestId, staffId);
//    }
}