package swp.auctionkoi.service.auction.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.request.Auction;
import swp.auctionkoi.dto.respone.AuctionResponse;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.mapper.AuctionMapper;
import swp.auctionkoi.models.AuctionRequest;
import swp.auctionkoi.models.Bid;
import swp.auctionkoi.models.enums.KoiStatus;
import swp.auctionkoi.repository.*;
import swp.auctionkoi.service.auction.AuctionService;

import java.util.HashMap;
import java.util.Optional;

@Service
public class AuctionServiceImpl implements AuctionService {
    @Autowired
    private AuctionRequestRepository auctionRequestRepository;

    @Autowired
    private KoiFishRepository koiFishRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private UserRepository userRepository;

    AuctionMapper auctionMapper;

    @Override
    public Optional<swp.auctionkoi.models.Auction> getAuction(int id) {
        return Optional.empty();
    }

    @Override
    public Optional<AuctionResponse> createAuction (Auction auctionDTO){
        AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionDTO.getAuctionId())
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_EXISTED));
        swp.auctionkoi.models.Auction auction = auctionMapper.toAuction(auctionDTO);
        auctionRepository.save(auction);
        return Optional.ofNullable(auctionMapper.toAuctionResponse(auction));
    }

    @Override
    public swp.auctionkoi.models.Auction updateAuction(swp.auctionkoi.models.Auction auction) {
        return null;
    }

    @Override
    public void deleteAuction(int id) {

    }

    @Override
    public void viewDetailAuction(int auctionId) {

    }

    @Override
    public HashMap<Integer, Bid> viewHistoryBidAuction(int auctionId) {
        return null;
    }
}
