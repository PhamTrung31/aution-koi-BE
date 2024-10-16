package swp.auctionkoi.service.auctionrequest;

import swp.auctionkoi.dto.request.AuctionRequest;
import swp.auctionkoi.dto.request.AuctionRequestUpdate;
import swp.auctionkoi.dto.respone.AuctionRequestResponse;
import swp.auctionkoi.dto.respone.AuctionResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

public interface AuctionRequestService {
    Optional<AuctionRequestResponse> sendAuctionRequest(AuctionRequest auctionRequest);
    HashMap<Integer, AuctionRequestResponse> viewAllAuctionRequest();
    HashMap<Integer, AuctionRequestResponse> viewAllAuctionRequestsForBreeder(int breederId);
    Optional<AuctionRequestResponse> viewAuctionRequestDetail(int auctionRequestId);
    Optional<AuctionRequestResponse> updateAuctionRequest (int auctionRequestId, AuctionRequestUpdate auctionRequestDTO);
    Optional<AuctionRequestResponse> cancelAuctionRequest(int auctionRequestId, int breederID);
    AuctionRequestResponse approveAuctionRequest(int auctionRequestId, int staffId, LocalDateTime auctionDateTime);
    AuctionRequestResponse rejectAuctionRequest (int auctionRequestId, int staffId);
    AuctionRequestResponse sendRequestUpdateDetailAuction(int auctionId, AuctionRequestUpdate auctionRequestUpdate);
    AuctionResponse approveRequestUpdateAuction(int auctionRequestId, int staffId);
}
