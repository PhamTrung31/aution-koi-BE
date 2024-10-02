package swp.auctionkoi.service.auctionrequest;

import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.request.AuctionRequestDTO;
import swp.auctionkoi.dto.request.AuctionRequestUpdateDTO;
import swp.auctionkoi.dto.respone.AuctionRequestResponse;
import swp.auctionkoi.dto.respone.AuctionResponseUpdate;
import swp.auctionkoi.models.AuctionRequest;

@Service
public interface AuctionRequestService {
    AuctionRequestResponse sendAuctionRequest(AuctionRequestDTO auctionRequestDto);
    AuctionResponseUpdate updateAuctionReuest(AuctionRequestUpdateDTO auctionRequestUpdateDTO);
}
