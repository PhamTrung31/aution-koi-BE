    package swp.auctionkoi.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.respone.ApiResponse;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.Bid;
import swp.auctionkoi.models.User;
import swp.auctionkoi.models.enums.Role;
import swp.auctionkoi.repository.BidRepository;
import swp.auctionkoi.repository.UserRepository;
import swp.auctionkoi.service.bid.BidService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bids")
@CrossOrigin("*")
public class BidController {

    @Autowired
    private BidRepository bidRepository;
    @Autowired
    private UserRepository userRepository;

//    @GetMapping("/top5")
//    public List<Bid> getTop5TraditionalBids() {
//        return bidService.getCachedTop5Bids();
//    }

    @GetMapping("/listBidOfUser/{auctionId}")
    public ApiResponse<List<Bid>> listBidOfUser(@PathVariable int auctionId) {

        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if(!user.getRole().equals(Role.MEMBER)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        List<Bid> bidOfUser = bidRepository.findListBidByAuctionIdAndUserId(auctionId, user.getId());

        return ApiResponse.<List<Bid>>builder()
                .code(200)
                .result(bidOfUser)
                .build();
    }
}
