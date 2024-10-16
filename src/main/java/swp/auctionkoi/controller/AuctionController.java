package swp.auctionkoi.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.request.auction.AuctionJoinRequest;

import swp.auctionkoi.dto.respone.ApiResponse;
import swp.auctionkoi.dto.respone.auction.AuctionJoinResponse;
import swp.auctionkoi.models.AuctionParticipants;
import swp.auctionkoi.service.auction.AuctionService;



@RestController
@RequiredArgsConstructor
@RequestMapping("/auctions")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuctionController {

    AuctionService auctionService;

    @PostMapping("/join")
    public ApiResponse<AuctionJoinResponse> joinAuction(@RequestBody AuctionJoinRequest request) {

        AuctionJoinResponse auctionParticipant = auctionService.JoinAuction(request.getUserId(), request.getAuctionId());

        AuctionJoinResponse response = AuctionJoinResponse.builder()
                .userId(request.getUserId())
                .auctionId(request.getAuctionId())
                .joinDate(auctionParticipant.getJoinDate())
                .build();

        return ApiResponse.<AuctionJoinResponse>builder()
                .result(response)
                .code(200)
                .message("Successfully")
                .build();
    }


    @PostMapping("/end/{auctionId}")
    public ApiResponse<String> endAuction(@PathVariable int auctionId) {
        try {
            auctionService.endAuction(auctionId);
            return ApiResponse.<String>builder()
                    .code(200)
                    .message("Successfully")
                    .build();
        } catch (RuntimeException ex) {
//            return ResponseEntity.badRequest().body(ex.getMessage());
            return ApiResponse.<String>builder()
                    .code(404)
                    .message(ex.getMessage())
                    .build();
        }
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


