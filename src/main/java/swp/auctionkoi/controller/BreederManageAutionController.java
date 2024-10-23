package swp.auctionkoi.controller;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.request.AuctionRequestDTO;
import swp.auctionkoi.dto.request.AuctionRequestResponseData;
import swp.auctionkoi.dto.respone.ApiResponse;
import swp.auctionkoi.dto.respone.AuctionRequestResponse;
import swp.auctionkoi.service.auctionrequest.AuctionRequestService;

import java.util.HashMap;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auctions")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BreederManageAutionController {

    AuctionRequestService auctionRequestService;

    @PostMapping("/send-request")
    public ApiResponse<AuctionRequestResponse> sendAuctionRequest(@RequestBody AuctionRequestDTO auctionRequestDTO) {
        AuctionRequestResponse auctionRequestResponse = auctionRequestService.sendAuctionRequest(auctionRequestDTO);
        return ApiResponse.<AuctionRequestResponse>builder()
                .code(200)
                .message(auctionRequestResponse.getMessage())
                .result(auctionRequestResponse)
                .build();
    }

    @GetMapping("/view-all-requests/{breederId}")
    public ApiResponse<HashMap<Integer, AuctionRequestResponseData>> viewAllAuctionRequestsForBreeder(@PathVariable Integer breederId) {
        HashMap<Integer, AuctionRequestResponseData> auctionRequestResponseDataHashMap = auctionRequestService.viewAllAuctionRequestsForBreeder(breederId);
        return ApiResponse.<HashMap<Integer, AuctionRequestResponseData>>builder()
                .code(200)
                .message("")
                .result(auctionRequestResponseDataHashMap)
                .build();
    }

    @PutMapping("/update/{auctionRequestId}")
    public ApiResponse<AuctionRequestResponse> updateAuctionRequest(@PathVariable Integer auctionRequestId, @RequestBody AuctionRequestDTO auctionRequestDTO){
        AuctionRequestResponse auctionRequestResponse = auctionRequestService.updateAuctionRequestForBreeder(auctionRequestId, auctionRequestDTO);

        log.info(auctionRequestResponse.getMessage());

        return ApiResponse.<AuctionRequestResponse>builder()
                .code(200)
                .message(auctionRequestResponse.getMessage())
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
