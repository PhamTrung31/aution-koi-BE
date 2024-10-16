package swp.auctionkoi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.request.Auction;
import swp.auctionkoi.dto.request.AuctionRequest;
import swp.auctionkoi.dto.request.AuctionRequestUpdate;
import swp.auctionkoi.dto.respone.AuctionRequestResponse;
import swp.auctionkoi.dto.respone.AuctionResponse;
import swp.auctionkoi.service.auction.AuctionService;
import swp.auctionkoi.service.auctionrequest.AuctionRequestService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auction")
public class AuctionController {

    private final AuctionRequestService auctionRequestService;
    private final AuctionService auctionService;

     @PostMapping("/send-request")
     public ResponseEntity<AuctionRequestResponse> sendAuctionRequest(@RequestBody AuctionRequest auctionRequest) {
         Optional<AuctionRequestResponse> auctionRequestResponseOptional = auctionRequestService.sendAuctionRequest(auctionRequest);
         if(auctionRequestResponseOptional.isPresent()){
             return ResponseEntity.ok(auctionRequestResponseOptional.get());
         }
     return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
     }

    @PostMapping("/send-request-update/{auctionId}")
    public AuctionRequestResponse sendRequestUpdateDetailAuction(@PathVariable Integer auctionId, @RequestBody AuctionRequestUpdate auctionRequestUpdate) {
        return auctionRequestService.sendRequestUpdateDetailAuction(auctionId, auctionRequestUpdate);
    }

    @GetMapping("/view-all-requests")
    public ResponseEntity<HashMap<Integer, AuctionRequestResponse>> viewAllAuctionRequest() {
        return ResponseEntity.ok(auctionRequestService.viewAllAuctionRequest());
    }

    @GetMapping("/view-request-detail/{auctionRequestId}")
    public ResponseEntity<AuctionRequestResponse> viewAuctionRequestDetail(@PathVariable Integer auctionRequestId) {
        Optional<AuctionRequestResponse> auctionRequestResponseOptional = auctionRequestService.viewAuctionRequestDetail(auctionRequestId);
        if(auctionRequestResponseOptional.isPresent()){
            return ResponseEntity.ok(auctionRequestResponseOptional.get());
        }
         return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/view-all-breeder-requests/{breederId}")
    public ResponseEntity<HashMap<Integer, AuctionRequestResponse>> viewAllAuctionRequestsForBreeder(@PathVariable Integer breederId) {
        return ResponseEntity.ok(auctionRequestService.viewAllAuctionRequestsForBreeder(breederId));
    }

     @PutMapping("/update/{auctionRequestId}")
    public ResponseEntity<AuctionRequestResponse> updateAuctionRequest(@PathVariable Integer auctionRequestId, @RequestBody AuctionRequestUpdate auctionRequestUpdate){
         Optional<AuctionRequestResponse> auctionRequestResponseOptional = auctionRequestService.updateAuctionRequest(auctionRequestId, auctionRequestUpdate);
         if(auctionRequestResponseOptional.isPresent()){
             return ResponseEntity.ok(auctionRequestResponseOptional.get());
         }
         return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
     }

     @PutMapping("/cancel/{auctionRequestId}")
     public ResponseEntity<AuctionRequestResponse> cancelAuctionRequest(@PathVariable int auctionRequestId, @PathVariable int breederId){
         Optional<AuctionRequestResponse> auctionRequestResponseOptional = auctionRequestService.cancelAuctionRequest(auctionRequestId, breederId);
         if(auctionRequestResponseOptional.isPresent()){
             return ResponseEntity.ok(auctionRequestResponseOptional.get());
         }
         return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
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
    public AuctionResponse createAuction(@RequestBody Auction auction){
         return auctionService.createAuction(auction);
     }
    @PutMapping("/approve-update/{auctionRequestId}")
    public AuctionResponse approveAuctionUpdate(@PathVariable Integer auctionRequestId, Integer staffId) {
        return auctionRequestService.approveRequestUpdateAuction(auctionRequestId, staffId);
    }
}
