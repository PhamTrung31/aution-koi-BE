package swp.auctionkoi.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.respone.ApiResponse;
import swp.auctionkoi.models.AuctionRequest;
import swp.auctionkoi.service.auctionrequest.AuctionRequestService;

@Slf4j
@RestController
@RequestMapping("/auction-request")
@CrossOrigin("*")
public class AuctionRequestController {
    @Autowired
    private AuctionRequestService auctionRequestService;

    @GetMapping("/details/{auctionId}")
    public ApiResponse<AuctionRequest> getAuctionRequest(@PathVariable int auctionId){
        return ApiResponse.<AuctionRequest>builder()
                .code(200)
                .message("OK")
                .result(auctionRequestService.getAuctionRequestDetailByAuctionId(auctionId))
                .build();
    }
}
