package swp.auctionkoi.service.auctionrequest;

import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.request.AuctionRequestDTO;
import swp.auctionkoi.dto.request.AuctionRequestUpdateDTO;
import swp.auctionkoi.dto.respone.AuctionRequestResponse;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;

public interface AuctionRequestService {
    AuctionRequestResponse sendAuctionRequest(AuctionRequestDTO auctionRequestDto);
    HashMap<Integer, AuctionRequestResponse> viewAllAuctionRequest();
    HashMap<Integer, AuctionRequestResponse> viewAllAuctionRequestsForBreeder(int breederId);
    AuctionRequestResponse viewAuctionRequestDetail(int auctionRequestId);
    AuctionRequestResponse updateAuctionRequest(int auctionRequestId, AuctionRequestUpdateDTO auctionRequestUpdateDto);
    AuctionRequestResponse cancelAuctionRequest (int auctionRequestId, int breederID);
    AuctionRequestResponse approveAuctionRequest(int auctionRequestId, int staffId, LocalDateTime auctionDateTime);
    AuctionRequestResponse rejectAuctionRequest (int auctionRequestId, int staffId);
}
