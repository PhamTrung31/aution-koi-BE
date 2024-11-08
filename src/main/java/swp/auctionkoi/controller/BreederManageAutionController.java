package swp.auctionkoi.controller;


import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.request.AuctionRequestDTO;
import swp.auctionkoi.dto.request.AuctionRequestResponseData;
import swp.auctionkoi.dto.respone.ApiResponse;
import swp.auctionkoi.dto.respone.AuctionRequestResponse;
import swp.auctionkoi.dto.respone.AuctionRequestUpdateResponse;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.AuctionRequest;
import swp.auctionkoi.models.User;
import swp.auctionkoi.models.enums.Role;
import swp.auctionkoi.repository.UserRepository;
import swp.auctionkoi.service.auctionrequest.AuctionRequestService;

import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/auctions")
@PreAuthorize("hasRole('BREEDER')")
public class BreederManageAutionController {

    @Autowired
    private AuctionRequestService auctionRequestService;

    @Autowired
    private UserRepository userRepository;

    private User getUserFromContext(){
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        return userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    @PostMapping("/send-request")
    public ApiResponse<AuctionRequest> sendAuctionRequest(@RequestBody @Valid AuctionRequestDTO auctionRequestDTO) {

        User breeder = getUserFromContext();

        if(!breeder.getRole().equals(Role.BREEDER)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        AuctionRequest auctionRequestResponse = auctionRequestService.sendAuctionRequest(breeder.getId(), auctionRequestDTO);
        return ApiResponse.<AuctionRequest>builder()
                .code(200)
                .message("Successfully sent auction request")
                .result(auctionRequestResponse)
                .build();
    }

    @GetMapping("/view-all-requests")
    public ApiResponse<List<AuctionRequest>> viewAllAuctionRequestsForBreeder() {

        User breeder = getUserFromContext();

        if(!breeder.getRole().equals(Role.BREEDER)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        List<AuctionRequest> auctionRequestResponseDataHashMap = auctionRequestService.viewAllAuctionRequestsForBreeder(breeder.getId());
        log.info("Check size auctionRequestResponseDataHashMap: {}", auctionRequestResponseDataHashMap.size());
        if (auctionRequestResponseDataHashMap.isEmpty()) {
            log.warn("No auction requests found for breederId: {}", breeder.getId());
            return ApiResponse.<List<AuctionRequest>>builder()
                    .code(204)  // No Content
                    .message("No auction requests found")
                    .build();
        }
        return ApiResponse.<List<AuctionRequest>>builder()
                .code(200)
                .message("")
                .result(auctionRequestResponseDataHashMap)
                .build();
    }

    @PutMapping("/update/{auctionRequestId}")
    public ApiResponse<AuctionRequest> updateAuctionRequest(@PathVariable Integer auctionRequestId, @RequestBody AuctionRequestDTO auctionRequestDTO){

        User breeder = getUserFromContext();

        if(!breeder.getRole().equals(Role.BREEDER)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        AuctionRequest auctionRequestResponse = auctionRequestService.updateAuctionRequestForBreeder(auctionRequestId, breeder.getId(), auctionRequestDTO);
        return ApiResponse.<AuctionRequest>builder()
                .code(200)
                .message("Update auction request successfully!")
                .result(auctionRequestResponse)
                .build();
    }

    @PutMapping("/cancel/{auctionRequestId}")
    public ApiResponse<AuctionRequestResponse> cancelAuctionRequest(@PathVariable Integer auctionRequestId){

        User breeder = getUserFromContext();

        if(!breeder.getRole().equals(Role.BREEDER)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        AuctionRequestResponse auctionRequestResponse = auctionRequestService.cancelAuctionRequest(auctionRequestId, breeder.getId());
        return  ApiResponse.<AuctionRequestResponse>builder()
                .code(200)
                .message(auctionRequestResponse.getMessage())
                .result(auctionRequestResponse)
                .build();
    }
}
