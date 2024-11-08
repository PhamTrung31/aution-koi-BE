package swp.auctionkoi.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.request.auction.AuctionJoinRequest;
import swp.auctionkoi.dto.request.bid.BidRequest;
import swp.auctionkoi.dto.request.bid.BidRequestTraditional;
import swp.auctionkoi.dto.respone.ApiResponse;
import swp.auctionkoi.dto.respone.auction.AuctionJoinResponse;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.AuctionRequest;
import swp.auctionkoi.models.User;
import swp.auctionkoi.models.enums.AuctionType;
import swp.auctionkoi.models.enums.Role;
import swp.auctionkoi.repository.AuctionRequestRepository;
import swp.auctionkoi.repository.UserRepository;
import swp.auctionkoi.service.auction.AnonymousAuctionService;
import swp.auctionkoi.service.auction.AuctionService;
import swp.auctionkoi.service.auction.FixedPriceAuctionService;
import swp.auctionkoi.service.auction.TraditionalAuctionService;
import swp.auctionkoi.service.auction.impl.AuctionServiceImpl;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auctions")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MemberManageAuctionController {
    TraditionalAuctionService traditionalAuctionService;
    AuctionRequestRepository auctionRequestRepository;
    FixedPriceAuctionService fixedPriceAuctionService;
    AnonymousAuctionService anonymousAuctionService;
    AuctionService auctionService;
    private final UserRepository userRepository;

    private User getUserFromContext(){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        return userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    @PostMapping("/join/{auctionId}")
    public ApiResponse<String> joinAuction(@PathVariable int auctionId) {

        User user = getUserFromContext();

        if(!user.getRole().equals(Role.MEMBER)){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        auctionService.joinAuction(user.getId(), auctionId);
        return ApiResponse.<String>builder()
                .code(200)
                .message("Join auction successfully!")
                .build();
    }

    @GetMapping("/check-participation/{auctionId}")
    public ApiResponse<String> checkAuctionParticipation(@PathVariable int auctionId) {

        User user = getUserFromContext();

        if(!user.getRole().equals(Role.MEMBER)){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        String participationStatus = auctionService.checkUserParticipationInAuction(user.getId(), auctionId);
        var context = SecurityContextHolder.getContext();
        return ApiResponse.<String>builder()
                .code(200)
                .message(participationStatus)
                .build();
    }

    @PostMapping("/placebid/traditional")
    public ApiResponse<String> placeBidTraditional(@RequestBody BidRequestTraditional bidRequestTraditional){

        User user = getUserFromContext();

        if(!user.getRole().equals(Role.MEMBER)){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        AuctionRequest auctionRequest = auctionRequestRepository.findByAuctionId(bidRequestTraditional.getAuctionId()).orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_FOUND));
        if(auctionRequest.getMethodType().equals(AuctionType.TRADITIONAL)){
            traditionalAuctionService.placeBid(user.getId(), bidRequestTraditional.getAuctionId(), bidRequestTraditional);
        }
        return ApiResponse.<String>builder()
                .code(200)
                .message("Place bid successfully.")
                .build();
    }

    @PostMapping("/placebid")
    public ApiResponse<String> placeBid(@RequestBody BidRequest bidRequest){

        User user = getUserFromContext();

        if(!user.getRole().equals(Role.MEMBER)){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        AuctionRequest auctionRequest = auctionRequestRepository.findByAuctionId(bidRequest.getAuctionId()).orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_FOUND));
        if(auctionRequest.getMethodType().equals(AuctionType.FIXED_PRICE)){
            fixedPriceAuctionService.placeBid(user.getId(), bidRequest.getAuctionId(), bidRequest);
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
