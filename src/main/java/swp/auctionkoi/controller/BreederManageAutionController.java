package swp.auctionkoi.controller;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.request.AuctionRequestDTO;
import swp.auctionkoi.dto.request.AuctionRequestResponseData;
import swp.auctionkoi.dto.respone.ApiResponse;
import swp.auctionkoi.dto.respone.AuctionRequestResponse;
import swp.auctionkoi.dto.respone.AuctionRequestUpdateResponse;
import swp.auctionkoi.models.AuctionRequest;
import swp.auctionkoi.service.auctionrequest.AuctionRequestService;

import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auctions")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('BREEDER')")
public class BreederManageAutionController {

    AuctionRequestService auctionRequestService;

    @PostMapping("/send-request")
    public ApiResponse<AuctionRequest> sendAuctionRequest(@RequestBody AuctionRequestDTO auctionRequestDTO) {
        AuctionRequest auctionRequestResponse = auctionRequestService.sendAuctionRequest(auctionRequestDTO);
        return ApiResponse.<AuctionRequest>builder()
                .code(200)
                .message("Successfully sent auction request")
                .result(auctionRequestResponse)
                .build();
    }

    @GetMapping("/view-all-requests/{breederId}")
    public ApiResponse<List<AuctionRequest>> viewAllAuctionRequestsForBreeder(@PathVariable Integer breederId) {
        List<AuctionRequest> auctionRequestResponseDataHashMap = auctionRequestService.viewAllAuctionRequestsForBreeder(breederId);
        log.info("Check size auctionRequestResponseDataHashMap: {}", auctionRequestResponseDataHashMap.size());
        if (auctionRequestResponseDataHashMap.isEmpty()) {
            log.warn("No auction requests found for breederId: {}", breederId);
            return ApiResponse.<List<AuctionRequest>>builder()
                    .code(204)  // No Content
                    .message("No auction requests found")
                    .build();
        }
        return ApiResponse.<List<AuctionRequest>>builder()
                .code(200)
                .message("")
                .result(auctionRequestResponseDataHashMap)
                .build();
    }

    @PutMapping("/update/{auctionRequestId}")
    public ApiResponse<AuctionRequest> updateAuctionRequest(@PathVariable Integer auctionRequestId, @RequestBody AuctionRequestDTO auctionRequestDTO){
        AuctionRequest auctionRequestResponse = auctionRequestService.updateAuctionRequestForBreeder(auctionRequestId, auctionRequestDTO);
        return ApiResponse.<AuctionRequest>builder()
                .code(200)
                .message("Update auction request successfully!")
                .result(auctionRequestResponse)
                .build();
    }

    @PutMapping("/cancel/{auctionRequestId}/{breederID}")
    public ApiResponse<AuctionRequestResponse> cancelAuctionRequest(@PathVariable Integer auctionRequestId, @PathVariable Integer breederID){
        AuctionRequestResponse auctionRequestResponse = auctionRequestService.cancelAuctionRequest(auctionRequestId, breederID);
        return  ApiResponse.<AuctionRequestResponse>builder()
                .code(200)
                .message(auctionRequestResponse.getMessage())
                .result(auctionRequestResponse)
                .build();
    }
}
