package swp.auctionkoi.service.auctionrequest;

import swp.auctionkoi.dto.request.AuctionRequestDTO;
import swp.auctionkoi.dto.request.AuctionRequestResponseData;
import swp.auctionkoi.dto.respone.AuctionRequestResponse;
import swp.auctionkoi.dto.respone.AuctionRequestUpdateResponse;
import swp.auctionkoi.models.AuctionRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public interface AuctionRequestService {
    AuctionRequestResponse sendAuctionRequest(AuctionRequestDTO auctionRequestDTO);
    HashMap<Integer, AuctionRequestResponseData> viewAllAuctionRequest();
    AuctionRequestResponseData viewAuctionRequestDetail(int auctionRequestId);
    HashMap<Integer, AuctionRequestResponseData> viewAllAuctionRequestsForBreeder(Integer breederId);
    AuctionRequestResponse updateAuctionRequestForBreeder(Integer auctionRequestId, AuctionRequestDTO auctionRequestDTO);
    AuctionRequestResponse cancelAuctionRequest (int auctionRequestId, int breederID);
    List<AuctionRequest> getAuctionRequestsInManagerReview();
    List<AuctionRequest> getAuctionRequestsInWait();
    AuctionRequestUpdateResponse approveAuctionRequestForStaff(int auctionRequestId, int staffId, boolean isSendToManager);
//    AuctionRequestResponse rejectAuctionRequestForStaff (int auctionRequestId, int staffId);
    AuctionRequestUpdateResponse reviewAuctionRequestByManager(int auctionRequestId, int managerId, Integer staffId, boolean isApproved, boolean assignToStaff);
    AuctionRequestUpdateResponse reviewAuctionRequestByStaff(int auctionRequestId, int staffId, boolean isApproved);
}
