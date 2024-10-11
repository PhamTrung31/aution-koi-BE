package swp.auctionkoi.service.auctionrequest;

import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.request.AuctionRequestDTO;
import swp.auctionkoi.dto.request.AuctionRequestUpdateDTO;
import swp.auctionkoi.dto.respone.AuctionRequestResponse;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
public interface AuctionRequestService {
    AuctionRequestResponse sendAuctionRequest(AuctionRequestDTO auctionRequestDto);
    AuctionRequestResponse updateAuctionRequest(int auctionRequestId, AuctionRequestUpdateDTO auctionRequestUpdateDto);
    AuctionRequestResponse approveAuctionRequest(int auctionRequestId, int staffId, LocalDateTime auctionDateTime);
    AuctionRequestResponse rejectAuctionRequest (int auctionRequestId, int staffId);
}
