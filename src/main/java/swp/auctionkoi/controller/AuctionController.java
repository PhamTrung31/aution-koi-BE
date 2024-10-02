package swp.auctionkoi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swp.auctionkoi.dto.request.AuctionRequestDTO;
import swp.auctionkoi.dto.respone.AuctionRequestResponse;
import swp.auctionkoi.service.auctionrequest.AuctionRequestService;
import swp.auctionkoi.service.auctionrequest.impl.AuctionRequestServiceImpl;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auction")
public class AuctionController {

    private final AuctionRequestService auctionRequestService;

     @PostMapping("/auction/request")
     public AuctionRequestResponse sendAuctionRequest(@RequestBody AuctionRequestDTO auctionRequestDTO) {
        return auctionRequestService.sendAuctionRequest(auctionRequestDTO);
     }
}
