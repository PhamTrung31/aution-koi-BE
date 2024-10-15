package swp.auctionkoi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.request.AuctionDTO;
import swp.auctionkoi.dto.request.AuctionRequestDTO;
import swp.auctionkoi.dto.request.AuctionRequestUpdateDTO;
import swp.auctionkoi.dto.respone.AuctionRequestResponse;
import swp.auctionkoi.dto.respone.AuctionResponse;
import swp.auctionkoi.service.auction.AuctionService;
import swp.auctionkoi.service.auctionrequest.AuctionRequestService;
import swp.auctionkoi.service.auctionrequest.impl.AuctionRequestServiceImpl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auction")
public class AuctionController {

    private final AuctionRequestService auctionRequestService;
    private final AuctionService auctionService;

     @PostMapping("/send-request")
     public AuctionRequestResponse sendAuctionRequest(@RequestBody AuctionRequestDTO auctionRequestDTO) {
        return auctionRequestService.sendAuctionRequest(auctionRequestDTO);
     }

    @PostMapping("/send-request-update/{auctionId}")
    public AuctionRequestResponse sendRequestUpdateDetailAuction(@PathVariable Integer auctionId, @RequestBody AuctionRequestUpdateDTO auctionRequestUpdateDTO) {
        return auctionRequestService.sendRequestUpdateDetailAuction(auctionId, auctionRequestUpdateDTO);
    }

    @GetMapping("/view-all-requests")
    public ResponseEntity<HashMap<Integer, AuctionRequestResponse>> viewAllAuctionRequest() {
        return ResponseEntity.ok(auctionRequestService.viewAllAuctionRequest());
    }

    @GetMapping("/view-request-detail/{auctionRequestId}")
    public ResponseEntity<AuctionRequestResponse> viewAuctionRequestDetail(@PathVariable Integer auctionRequestId) {
        return ResponseEntity.ok(auctionRequestService.viewAuctionRequestDetail(auctionRequestId));
    }

    @GetMapping("/view-all-breeder-requests/{breederId}")
    public ResponseEntity<HashMap<Integer, AuctionRequestResponse>> viewAllAuctionRequestsForBreeder(@PathVariable Integer breederId) {
        return ResponseEntity.ok(auctionRequestService.viewAllAuctionRequestsForBreeder(breederId));
    }

     @PutMapping("/update/{auctionRequestId}")
    public AuctionRequestResponse updateAuctionRequest(@PathVariable Integer auctionRequestId, @RequestBody AuctionRequestUpdateDTO auctionRequestUpdateDTO){
         return  auctionRequestService.updateAuctionRequest(auctionRequestId, auctionRequestUpdateDTO);
     }

     @PutMapping("/cancel/{auctionRequestId}")
     public AuctionRequestResponse cancelAuctionRequest(@PathVariable Integer auctionRequestId, Integer breederID){
         return  auctionRequestService.cancelAuctionRequest(auctionRequestId, breederID);
     }

     @PutMapping("/approve/{auctionRequestId}")
     public AuctionRequestResponse approveAuctionRequest(@PathVariable Integer auctionRequestId, Integer staffId, @RequestBody LocalDateTime auctionDateTime){
         return auctionRequestService.approveAuctionRequest(auctionRequestId, staffId, auctionDateTime);
     }

     @PutMapping("/reject/{auctionRequestId}")
     public AuctionRequestResponse rejectAuctionRequest (@PathVariable Integer auctionRequestId, @RequestHeader("staffId") Integer staffId){
         return auctionRequestService.rejectAuctionRequest(auctionRequestId, staffId);
     }

     @PostMapping("/booking")
    public AuctionResponse createAuction(@RequestBody AuctionDTO auctionDTO){
         return auctionService.createAuction(auctionDTO);
     }
    @PutMapping("/approve-update/{auctionRequestId}")
    public AuctionResponse approveAuctionUpdate(@PathVariable Integer auctionRequestId, Integer staffId) {
        return auctionRequestService.approveRequestUpdateAuction(auctionRequestId, staffId);
    }
}
