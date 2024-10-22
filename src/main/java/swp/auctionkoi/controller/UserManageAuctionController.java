package swp.auctionkoi.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swp.auctionkoi.dto.request.auction.AuctionJoinRequest;
import swp.auctionkoi.dto.request.bid.BidRequest;
import swp.auctionkoi.dto.request.bid.BidRequestTraditional;
import swp.auctionkoi.dto.respone.ApiResponse;
import swp.auctionkoi.dto.respone.auction.AuctionJoinResponse;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.AuctionRequest;
import swp.auctionkoi.models.enums.AuctionType;
import swp.auctionkoi.repository.AuctionRequestRepository;
import swp.auctionkoi.service.auction.AnonymousAuctionService;
import swp.auctionkoi.service.auction.FixedPriceAuctionService;
import swp.auctionkoi.service.auction.TraditionalAuctionService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auctions")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserManageAuctionController {
    TraditionalAuctionService traditionalAuctionService;
    AuctionRequestRepository auctionRequestRepository;
    FixedPriceAuctionService fixedPriceAuctionService;
    AnonymousAuctionService anonymousAuctionService;

    @PostMapping("/join")
    public ResponseEntity<AuctionJoinResponse> joinAuction(@RequestBody AuctionJoinRequest request) {
        return null;
    }

    @PostMapping("/placebid/traditional")
    public ApiResponse<String> placeBidTraditional(@RequestBody BidRequestTraditional bidRequestTraditional){
        AuctionRequest auctionRequest = auctionRequestRepository.findByAuctionId(bidRequestTraditional.getAuctionId()).orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_FOUND));
        if(auctionRequest.getMethodType().equals(AuctionType.TRADITIONAL)){
            traditionalAuctionService.placeBid(bidRequestTraditional.getAuctionId(), bidRequestTraditional);
        }
        return ApiResponse.<String>builder()
                .code(200)
                .message("Place bid successfully.")
                .build();
    }

    @PostMapping("/placebid")
    public ApiResponse<String> placeBid(@RequestBody BidRequest bidRequest){
        AuctionRequest auctionRequest = auctionRequestRepository.findByAuctionId(bidRequest.getAuctionId()).orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_FOUND));
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
