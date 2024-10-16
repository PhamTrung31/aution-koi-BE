package swp.auctionkoi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.request.Auction;
import swp.auctionkoi.dto.request.AuctionRequest;
import swp.auctionkoi.dto.request.AuctionRequestUpdate;
import swp.auctionkoi.dto.respone.ApiResponse;
import swp.auctionkoi.dto.respone.AuctionRequestResponse;
import swp.auctionkoi.dto.respone.AuctionResponse;
import swp.auctionkoi.service.auction.AuctionService;
import swp.auctionkoi.service.auctionrequest.AuctionRequestService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
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
    public ApiResponse<HashMap<Integer, AuctionRequestResponse>> viewAllAuctionRequest() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Username {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info("GrantedAuthority {}", grantedAuthority));

        return ApiResponse.<HashMap<Integer, AuctionRequestResponse>>builder()
                .result(auctionRequestService.viewAllAuctionRequest())
                .build();
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
     public ResponseEntity<AuctionRequestResponse> cancelAuctionRequest(@PathVariable int auctionRequestId){
         Optional<AuctionRequestResponse> auctionRequestResponseOptional = auctionRequestService.cancelAuctionRequest(auctionRequestId);
         if(auctionRequestResponseOptional.isPresent()){
             return ResponseEntity.ok(auctionRequestResponseOptional.get());
         }
         return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
     }

     @PatchMapping("/approve/{auctionRequestId}")
     public ResponseEntity<AuctionRequestResponse> approveAuctionRequest(@PathVariable int auctionRequestId){
         Optional<AuctionRequestResponse> auctionRequestResponseOptional = auctionRequestService.approveAuctionRequest(auctionRequestId);
         if(auctionRequestResponseOptional.isPresent()){
             return ResponseEntity.ok(auctionRequestResponseOptional.get());
         }
         return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
     }

     @PatchMapping("/reject/{auctionRequestId}")
     public ResponseEntity<AuctionRequestResponse> rejectAuctionRequest(@PathVariable int auctionRequestId){
         Optional<AuctionRequestResponse> auctionRequestResponseOptional = auctionRequestService.rejectAuctionRequest(auctionRequestId);
         if (auctionRequestResponseOptional.isPresent()) {
             return ResponseEntity.ok(auctionRequestResponseOptional.get());
         }
         return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
     }

     @PostMapping("/booking")
    public ResponseEntity<AuctionResponse>createAuction (Auction auctionDTO){
         Optional<AuctionResponse> auctionResponseOptional = auctionService.createAuction(auctionDTO);
         if(auctionResponseOptional.isPresent()){
             return ResponseEntity.ok(auctionResponseOptional.get());
         }
         return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
     }

    @PutMapping("/approve-update/{auctionRequestId}")
    public AuctionResponse approveAuctionUpdate(@PathVariable Integer auctionRequestId, Integer staffId) {
        return auctionRequestService.approveRequestUpdateAuction(auctionRequestId, staffId);
    }
}
