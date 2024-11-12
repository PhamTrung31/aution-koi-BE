package swp.auctionkoi.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.request.*;
import swp.auctionkoi.dto.respone.ApiResponse;

import swp.auctionkoi.dto.respone.AuctionRequestUpdateResponse;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.Auction;
import swp.auctionkoi.dto.respone.auction.AuctionHistoryResponse;
import swp.auctionkoi.models.AuctionRequest;
import swp.auctionkoi.models.Bid;
import swp.auctionkoi.models.User;
import swp.auctionkoi.models.enums.Role;
import swp.auctionkoi.repository.BidRepository;
import swp.auctionkoi.repository.UserRepository;
import swp.auctionkoi.service.auction.AuctionService;
import swp.auctionkoi.service.auctionrequest.AuctionRequestService;
import swp.auctionkoi.service.koifish.impl.KoiFishServiceImpl;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/auctions")
@CrossOrigin("*")
public class AuctionController {

    @Autowired
    private AuctionRequestService auctionRequestService;

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private KoiFishServiceImpl koiFishService;

    @Autowired
    private BidRepository bidRepository;


    @GetMapping("/past-auction")
    public ApiResponse<List<AuctionHistoryResponse>> getAuctionComplete() {
        return ApiResponse.<List<AuctionHistoryResponse>>builder()
                .code(200)
                .message("OK")
                .result(auctionService.getListAuctionComplete())
                .build();
    }

    @GetMapping("/view-past-auction")
    public ApiResponse<List<AuctionHistoryResponse>> viewPastAuction() {
        return ApiResponse.<List<AuctionHistoryResponse>>builder()
                .code(200)
                .message("Successfully")
                .result(auctionService.getListAuctionComplete())
                .build();
    }


    @GetMapping("/detail/{id}")
    public ApiResponse<Auction> getAuctionById(@PathVariable int id) {
        Auction auction = auctionService.getAuctionById(id);
        return ApiResponse.<Auction>builder()
                .code(200)
                .message("Successfully")
                .result(auction)
                .build();
    }

    @GetMapping("/listBidOfUser/{auctionId}/{userId}")
    public ApiResponse<List<Bid>> listBidOfUser(@PathVariable int auctionId, @PathVariable int userId) {
        List<Bid> bidOfUser = bidRepository.findListBidByAuctionIdAndUserId(auctionId, userId);

        return ApiResponse.<List<Bid>>builder()
                .code(200)
                .result(bidOfUser)
                .build();
    }


//    @PutMapping("/reject/{auctionRequestId}")
//    public AuctionRequestResponse rejectAuctionRequestForStaff(@PathVariable Integer auctionRequestId, @RequestHeader("staffId") Integer staffId) {
//        return auctionRequestService.rejectAuctionRequestForStaff(auctionRequestId, staffId);
//    }
}