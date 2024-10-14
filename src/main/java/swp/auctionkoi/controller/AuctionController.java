package swp.auctionkoi.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.request.AuctionDTO;
import swp.auctionkoi.dto.request.AuctionRequestDTO;
import swp.auctionkoi.dto.request.AuctionRequestUpdateDTO;
import swp.auctionkoi.dto.respone.AuctionRequestResponse;
import swp.auctionkoi.dto.respone.AuctionResponse;
import swp.auctionkoi.dto.request.auction.AuctionJoinRequest;

import swp.auctionkoi.dto.respone.auction.AuctionJoinResponse;
import swp.auctionkoi.models.AuctionParticipants;
import swp.auctionkoi.service.auction.AuctionService;
import swp.auctionkoi.service.auctionrequest.AuctionRequestService;
import swp.auctionkoi.service.auctionrequest.impl.AuctionRequestServiceImpl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auctions")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuctionController {

    private final AuctionRequestService auctionRequestService;
    private final AuctionService auctionService;
    AuctionService auctionService;

    @PostMapping("/join")
    public ResponseEntity<AuctionJoinResponse> joinAuction(@RequestBody AuctionJoinRequest request) {
     @PostMapping("/send-request")
     public AuctionRequestResponse sendAuctionRequest(@RequestBody AuctionRequestDTO auctionRequestDTO) {
        return auctionRequestService.sendAuctionRequest(auctionRequestDTO);
     }

        AuctionJoinResponse auctionParticipant = auctionService.JoinAuction(request.getUserId(), request.getAuctionId());
    @GetMapping("/view-all-requests")
    public ResponseEntity<HashMap<Integer, AuctionRequestResponse>> viewAllAuctionRequest() {
        return ResponseEntity.ok(auctionRequestService.viewAllAuctionRequest());
    }

        AuctionJoinResponse response = AuctionJoinResponse.builder()
                .userId(request.getUserId())
                .auctionId(request.getAuctionId())
                .joinDate(auctionParticipant.getJoinDate())
                .build();
    @GetMapping("/view-request-detail/{auctionRequestId}")
    public ResponseEntity<AuctionRequestResponse> viewAuctionRequestDetail(@PathVariable Integer auctionRequestId) {
        return ResponseEntity.ok(auctionRequestService.viewAuctionRequestDetail(auctionRequestId));
    }

        return ResponseEntity.ok(response);
    @GetMapping("/view-all-breeder-requests/{breederId}")
    public ResponseEntity<HashMap<Integer, AuctionRequestResponse>> viewAllAuctionRequestsForBreeder(@PathVariable Integer breederId) {
        return ResponseEntity.ok(auctionRequestService.viewAllAuctionRequestsForBreeder(breederId));
    }

     @PutMapping("/update/{auctionRequestId}")
    public AuctionRequestResponse updateAuctionRequest(@PathVariable Integer auctionRequestId, @RequestBody AuctionRequestUpdateDTO auctionRequestUpdateDTO){
         return  auctionRequestService.updateAuctionRequest(auctionRequestId, auctionRequestUpdateDTO);
     }

    @PostMapping("/end/{auctionId}")
    public ResponseEntity<String> endAuction(@PathVariable int auctionId) {
        try {
            auctionService.endAuction(auctionId);
            return ResponseEntity.ok("Auction ended successfully.");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
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

}
     @PostMapping("/booking")
    public AuctionResponse createAuction(@RequestBody AuctionDTO auctionDTO){
         return auctionService.createAuction(auctionDTO);
     }
}

//
//    @Autowired
//    private AuctionService auctionService;
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Auction> getAuctionById(@PathVariable int id) {
//        Optional<Auction> auction = auctionService.getAuction(id);
//        return auction.map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }

    // Add a new auction (staff only)
//    @PostMapping

//    public ResponseEntity<Auction> addAuction(@RequestBody Auction auction) {
//        Auction newAuction = auctionService.addAuction(auction);
//        return new ResponseEntity<>(newAuction, HttpStatus.CREATED);
//    }
//
//    // Update an existing auction (staff only)
//    @PutMapping("/{id}")
//
//    public ResponseEntity<Auction> updateAuction(@PathVariable int id, @RequestBody Auction auction) {
//        auction.setId(id);  // Ensure the ID is set correctly
//        Auction updatedAuction = auctionService.updateAuction(auction);
//        return ResponseEntity.ok(updatedAuction);
//    }
//
//    // Delete an auction (staff only)
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteAuction(@PathVariable int id) {
//        auctionService.deleteAuction(id);
//        return ResponseEntity.noContent().build();
//    }


