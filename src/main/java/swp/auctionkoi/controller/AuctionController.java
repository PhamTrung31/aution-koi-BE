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
import swp.auctionkoi.dto.respone.ApiResponse;
import swp.auctionkoi.dto.respone.AuctionRequestResponse;
import swp.auctionkoi.dto.request.auction.AuctionJoinRequest;

import swp.auctionkoi.dto.respone.auction.AuctionJoinResponse;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auctions")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuctionController {

    AuctionRequestService auctionRequestService;
    AuctionService auctionService;
    AuctionScheduler auctionScheduler;
    private final AuctionRepository auctionRepository;
    private final AuctionRequestRepository auctionRequestRepository;
    private final TraditionalAuctionService traditionalAuctionService;
    private final FixedPriceAuctionService fixedPriceAuctionService;
    private final AnonymousAuctionService anonymousAuctionService;

    @PostMapping("/join")
    public ResponseEntity<AuctionJoinResponse> joinAuction(@RequestBody AuctionJoinRequest request) {
        return null;
    }

    @PostMapping("/start")
    public ApiResponse<String> startAuctions() {
        auctionScheduler.checkAuctions();
        return ApiResponse.<String>builder()
                .message("Auction check started.")
                .build();
    }

     @PostMapping("/send-request")
     public AuctionRequestResponse sendAuctionRequest(@RequestBody AuctionRequestDTO auctionRequestDTO) {
//        return auctionRequestService.sendAuctionRequest(auctionRequestDTO);
         return null;
     }

//        AuctionJoinResponse auctionParticipant = auctionService.JoinAuction(request.getUserId(), request.getAuctionId());
    @GetMapping("/view-all-requests")
    public ResponseEntity<HashMap<Integer, AuctionRequestResponse>> viewAllAuctionRequest() {
//        return ResponseEntity.ok(auctionRequestService.viewAllAuctionRequest());
        return null;
    }

//        AuctionJoinResponse response = AuctionJoinResponse.builder()
//                .userId(request.getUserId())
//                .auctionId(request.getAuctionId())
//                .joinDate(auctionParticipant.getJoinDate())
//                .build();
    @GetMapping("/view-request-detail/{auctionRequestId}")
    public ResponseEntity<AuctionRequestResponse> viewAuctionRequestDetail(@PathVariable Integer auctionRequestId) {
//        return ResponseEntity.ok(auctionRequestService.viewAuctionRequestDetail(auctionRequestId));
        return null;
    }

//        return ResponseEntity.ok(response);
    @GetMapping("/view-all-breeder-requests/{breederId}")
    public ResponseEntity<HashMap<Integer, AuctionRequestResponse>> viewAllAuctionRequestsForBreeder(@PathVariable Integer breederId) {
//        return ResponseEntity.ok(auctionRequestService.viewAllAuctionRequestsForBreeder(breederId));
        return null;
    }

     @PutMapping("/update/{auctionRequestId}")
    public AuctionRequestResponse updateAuctionRequest(@PathVariable Integer auctionRequestId, @RequestBody AuctionRequestResponseData auctionRequestResponseData){
//         return  auctionRequestService.updateAuctionRequestForBreeder(auctionRequestId, auctionRequestResponseData);
         return null;
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
//         return auctionRequestService.approveAuctionRequest(auctionRequestId, staffId, auctionDateTime);
         return null;
     }

     @PutMapping("/reject/{auctionRequestId}")
     public AuctionRequestResponse rejectAuctionRequest (@PathVariable Integer auctionRequestId, @RequestHeader("staffId") Integer staffId){
//         return auctionRequestService.rejectAuctionRequest(auctionRequestId, staffId);
         return null;
     }

     @PostMapping("/placebid")
    public ApiResponse<String> placeBid(@RequestBody BidRequest bidRequest){
         AuctionRequest auctionRequest = auctionRequestRepository.findByAuctionId(bidRequest.getAuctionId()).orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_FOUND));
         if(auctionRequest.getMethodType().equals(AuctionType.TRADITIONAL)){
             traditionalAuctionService.placeBid(bidRequest.getAuctionId(), bidRequest);
         }
         if(auctionRequest.getMethodType().equals(AuctionType.FIXED_PRICE)){
             fixedPriceAuctionService.placeBid(bidRequest.getAuctionId(), bidRequest);
         }
         if(auctionRequest.getMethodType().equals(AuctionType.ANONYMOUS)){
             anonymousAuctionService.placeBid(bidRequest.getAuctionId(), bidRequest);
         }
         return ApiResponse.<String>builder()
                 .code(200)
                 .message("Place bid successfully.")
                 .build();
     }
}
//     @PostMapping("/booking")
//    public AuctionResponse createAuction(@RequestBody AuctionDTO auctionDTO){
//         return auctionService.createAuction(auctionDTO);
//     }


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


