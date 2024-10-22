package swp.auctionkoi.service.auctionrequest;

import swp.auctionkoi.dto.request.AuctionRequestDTO;
import swp.auctionkoi.dto.request.AuctionRequestResponseData;
import swp.auctionkoi.dto.respone.AuctionRequestResponse;

import java.time.LocalDateTime;
import java.util.HashMap;

public interface AuctionRequestService {
    AuctionRequestResponse sendAuctionRequest(AuctionRequestDTO auctionRequestDTO);
    HashMap<Integer, AuctionRequestResponseData> viewAllAuctionRequest();
    AuctionRequestResponseData viewAuctionRequestDetail(int auctionRequestId);
    HashMap<Integer, AuctionRequestResponseData> viewAllAuctionRequestsForBreeder(Integer breederId);
    AuctionRequestResponse updateAuctionRequestForBreeder(Integer auctionRequestId, AuctionRequestDTO auctionRequestDTO);
    AuctionRequestResponse cancelAuctionRequest (int auctionRequestId, int breederID);
    AuctionRequestResponse approveAuctionRequestForStaff(int auctionRequestId, int staffId);
    AuctionRequestResponse rejectAuctionRequestForStaff (int auctionRequestId, int staffId);
}
