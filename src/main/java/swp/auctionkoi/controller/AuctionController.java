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
            return ApiResponse.<String>builder()
                    .code(404)
                    .message(ex.getMessage())
                    .build();
        }
    }


}



