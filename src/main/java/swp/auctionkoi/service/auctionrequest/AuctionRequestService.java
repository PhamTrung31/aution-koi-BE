package swp.auctionkoi.service.auctionrequest;

import swp.auctionkoi.dto.request.AuctionRequestDTO;
import swp.auctionkoi.dto.request.AuctionRequestResponseData;
import swp.auctionkoi.dto.respone.AuctionRequestResponse;
import swp.auctionkoi.dto.respone.AuctionRequestUpdateResponse;
import swp.auctionkoi.models.AuctionRequest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public interface AuctionRequestService {
    AuctionRequest getAuctionRequestDetailByAuctionId(int auctionId);

    AuctionRequest sendAuctionRequest(AuctionRequestDTO auctionRequestDTO);

    List<AuctionRequest> viewAllAuctionRequest();

    AuctionRequestResponseData viewAuctionRequestDetail(int auctionRequestId);

    List<AuctionRequest> viewAllAuctionRequestsForBreeder(Integer breederId);

    AuctionRequest updateAuctionRequestForBreeder(Integer auctionRequestId, AuctionRequestDTO auctionRequestDTO);

    AuctionRequestResponse cancelAuctionRequest(int auctionRequestId, int breederID);

    List<AuctionRequest> getAuctionRequestsInManagerReview();

    List<AuctionRequest> getAuctionRequestsInWait();

    List<AuctionRequest> getAuctionRequestsInAssignedToStaff(Integer staffId);

    List<AuctionRequest> getAuctionRequestsInAwaitingSchedule(Integer staffId);

    List<AuctionRequest> getAuctionRequestsByAssignedToStaff(Integer staffId);

    List<AuctionRequest> getAuctionRequestsInAwaitingSchedule();

    // For Staff initial review
    AuctionRequestUpdateResponse SendToManager(int auctionRequestId, int staffId);

    AuctionRequestUpdateResponse approveDirectlyByStaff(int auctionRequestId, int staffId);
//    AuctionRequestUpdateResponse rejectByStaff(int auctionRequestId, int staffId);

    // For Manager review
//    AuctionRequestUpdateResponse approveByManager(int auctionRequestId, int managerId, Integer staffId);
    AuctionRequestUpdateResponse rejectByManager(int auctionRequestId, int managerId);

    AuctionRequestUpdateResponse assignToStaffByManager(int auctionRequestId, int managerId, Integer staffId);

    // For Staff after manager assignment
    AuctionRequestUpdateResponse approveByAssignedStaff(int auctionRequestId, int staffId);

    AuctionRequestUpdateResponse rejectByAssignedStaff(int auctionRequestId, int staffId);

    AuctionRequestUpdateResponse scheduleAuction(int auctionRequestId, int staffId, Instant startTime, Instant endTime);
}
