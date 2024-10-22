package swp.auctionkoi.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.request.AuctionRequestDTO;
import swp.auctionkoi.dto.request.AuctionRequestResponseData;
import swp.auctionkoi.dto.request.bid.BidRequest;
import swp.auctionkoi.dto.request.bid.BidRequestTraditional;
import swp.auctionkoi.dto.respone.ApiResponse;
import swp.auctionkoi.dto.respone.AuctionRequestResponse;
import swp.auctionkoi.dto.request.auction.AuctionJoinRequest;

import swp.auctionkoi.dto.respone.auction.AuctionJoinResponse;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.AuctionRequest;
import swp.auctionkoi.models.enums.AuctionType;
import swp.auctionkoi.repository.AuctionRepository;
import swp.auctionkoi.repository.AuctionRequestRepository;
import swp.auctionkoi.service.auction.*;
import swp.auctionkoi.service.auctionrequest.AuctionRequestService;

import java.time.LocalDateTime;
import java.util.HashMap;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auctions")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuctionController {

    AuctionRequestService auctionRequestService;

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

    @PutMapping("/approve/{auctionRequestId}")
    public ApiResponse<AuctionRequestResponse> approveAuctionRequestForStaff(@PathVariable Integer auctionRequestId, Integer staffId) {
        AuctionRequestResponse auctionRequestResponse = auctionRequestService.approveAuctionRequestForStaff(auctionRequestId, staffId);

        return ApiResponse.<AuctionRequestResponse>builder()
                .code(200)
                .message(auctionRequestResponse.getMessage())
                .result(auctionRequestResponse)
                .build();
    }

    @PutMapping("/reject/{auctionRequestId}")
    public AuctionRequestResponse rejectAuctionRequestForStaff(@PathVariable Integer auctionRequestId, @RequestHeader("staffId") Integer staffId) {
        return auctionRequestService.rejectAuctionRequestForStaff(auctionRequestId, staffId);
    }
}