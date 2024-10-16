package swp.auctionkoi.service.auctionrequest.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.request.AuctionRequest;
import swp.auctionkoi.dto.request.AuctionRequestUpdate;
import swp.auctionkoi.dto.request.koifish.KoiFishCreateRequest;
import swp.auctionkoi.dto.respone.AuctionRequestResponse;
import swp.auctionkoi.dto.respone.AuctionResponse;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.mapper.AuctionRequestMapper;
import swp.auctionkoi.mapper.KoiFishMapper;
import swp.auctionkoi.models.Auction;
import swp.auctionkoi.models.KoiFish;
import swp.auctionkoi.models.User;
import swp.auctionkoi.models.enums.Role;
import swp.auctionkoi.repository.*;
import swp.auctionkoi.service.auctionrequest.AuctionRequestService;
import swp.auctionkoi.models.enums.KoiStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuctionRequestServiceImpl implements AuctionRequestService {
    @Autowired
    private AuctionRequestRepository auctionRequestRepository;

    @Autowired
    private KoiFishRepository koiFishRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private UserRepository userRepository;

    AuctionRequestMapper auctionRequestMapper;

    KoiFishMapper koiFishMapper;

    @Override
    public HashMap<Integer, AuctionRequestResponse> viewAllAuctionRequest(){
        HashMap<Integer, AuctionRequestResponse> auctionRequests = new HashMap<>();
        List<swp.auctionkoi.models.AuctionRequest> auctionRequestList = auctionRequestRepository.findAll();
        auctionRequestList.forEach(auctionRequest -> {
            AuctionRequestResponse auctionRequestResponse = auctionRequestMapper.toAuctionRequestResponse(auctionRequest);
            auctionRequests.put(auctionRequest.getId(), auctionRequestResponse);
        });
        return auctionRequests;
    }

    @Override
    public Optional<AuctionRequestResponse> viewAuctionRequestDetail(int auctionRequestId) {
        swp.auctionkoi.models.AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId).get();
        return Optional.ofNullable(auctionRequestMapper.toAuctionRequestResponse(auctionRequest));
    }

    @Override
    public HashMap<Integer, AuctionRequestResponse> viewAllAuctionRequestsForBreeder(int breederId) {
        User user = userRepository.findById(breederId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        HashMap<Integer, AuctionRequestResponse> auctionRequests = new HashMap<>();

        List<swp.auctionkoi.models.AuctionRequest> auctionRequestList = auctionRequestRepository.findByBreederId(breederId);

        auctionRequestList.forEach(auctionRequest -> {
            AuctionRequestResponse auctionRequestResponse = auctionRequestMapper.toAuctionRequestResponse(auctionRequest);
            auctionRequests.put(auctionRequest.getId(), auctionRequestResponse);
        });

        return auctionRequests;
    }

    @Override
    public Optional<AuctionRequestResponse> sendAuctionRequest(AuctionRequest auctionRequestDto) {
        KoiFish koiFish = koiFishRepository.findById(auctionRequestDto.getFishId())
                .orElseThrow(() -> new AppException(ErrorCode.FISH_NOT_EXISTED));
        User user = userRepository.findById(auctionRequestDto.getBreederId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if(koiFish == null)
        {
            KoiFishCreateRequest koiFishCreateRequest = new KoiFishCreateRequest();
            koiFish = koiFishMapper.toKoiFish(koiFishCreateRequest);
            koiFishRepository.save(koiFish);
        }
        swp.auctionkoi.models.AuctionRequest auctionRequest = auctionRequestMapper.toAuctionRequest(auctionRequestDto);
        auctionRequest.setRequestStatus(KoiStatus.NEW.ordinal());
        auctionRequestRepository.save(auctionRequest);

        return Optional.ofNullable(auctionRequestMapper.toAuctionRequestResponse(auctionRequest));
    }

    @Override
    public Optional<AuctionRequestResponse> updateAuctionRequest (int auctionRequestId, AuctionRequestUpdate auctionRequestDTO) {

        swp.auctionkoi.models.AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId).get();
        if(auctionRequest != null){
            auctionRequestMapper.updateAuctionRequest(auctionRequest, auctionRequestDTO);
            auctionRequestRepository.save(auctionRequest);
            return Optional.of(auctionRequestMapper.toAuctionRequestResponse(auctionRequestRepository.save(auctionRequest)));
        }
        return Optional.empty();
    }

    @Override
    public Optional<AuctionRequestResponse> cancelAuctionRequest(int auctionRequestId){

        swp.auctionkoi.models.AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_EXISTED));

        if (!auctionRequest.getRequestStatus().equals(KoiStatus.CANCELED) || !auctionRequest.getRequestStatus().equals(KoiStatus.LISTED_FOR_AUCTION)) {
            auctionRequest.setRequestStatus(KoiStatus.CANCELED.ordinal());
            auctionRequest.setRequestUpdatedDate(LocalDateTime.now());
            auctionRequestRepository.save(auctionRequest);
            AuctionRequestResponse auctionRequestResponse = auctionRequestMapper.toAuctionRequestResponse(auctionRequest);
            return Optional.of(auctionRequestResponse);
        }
        return Optional.empty();
    }

    @Override
    public Optional<AuctionRequestResponse> approveAuctionRequest(int auctionRequestId) {

        swp.auctionkoi.models.AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_EXISTED));

        if (!auctionRequest.getRequestStatus().equals(KoiStatus.APPROVED) || !auctionRequest.getRequestStatus().equals(KoiStatus.REJECTED)) {
            auctionRequest.setRequestStatus(KoiStatus.APPROVED.ordinal());
            auctionRequest.setRequestUpdatedDate(LocalDateTime.now());
            auctionRequestRepository.save(auctionRequest);
            AuctionRequestResponse auctionRequestResponse = auctionRequestMapper.toAuctionRequestResponse(auctionRequest);
            return Optional.of(auctionRequestResponse);
        }
        return Optional.empty();
    }

    @Override
    public Optional<AuctionRequestResponse> rejectAuctionRequest(int auctionRequestId) {

        swp.auctionkoi.models.AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_EXISTED));

        if (!auctionRequest.getRequestStatus().equals(KoiStatus.REJECTED)) {
            auctionRequest.setRequestStatus(KoiStatus.REJECTED.ordinal());
            auctionRequest.setRequestUpdatedDate(LocalDateTime.now());
            auctionRequestRepository.save(auctionRequest);
            AuctionRequestResponse auctionRequestResponse = auctionRequestMapper.toAuctionRequestResponse(auctionRequest);
            return Optional.of(auctionRequestResponse);
        }
        return Optional.empty();
    }

    //NOT USING MAPPER
    @Override
    public AuctionRequestResponse sendRequestUpdateDetailAuction(int auctionId, AuctionRequestUpdate auctionRequestUpdate) {

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_NOT_EXISTED));

        User user = userRepository.findById(auctionRequestUpdate.getBreeder().getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (auction.getStatus().equals(KoiStatus.LISTED_FOR_AUCTION) || auction.getStatus().equals(KoiStatus.CANCELED)) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        swp.auctionkoi.models.AuctionRequest auctionRequest = new swp.auctionkoi.models.AuctionRequest();
        auctionRequest.setId(auction.getId());
        auctionRequest.setBreeder(auctionRequestUpdate.getBreeder());
        auctionRequest.setFish(auctionRequestUpdate.getFish());
        auctionRequest.setBuyOut(auctionRequestUpdate.getBuyOut());
        auctionRequest.setStartPrice(auctionRequestUpdate.getStartPrice());
        auctionRequest.setIncrementPrice(auctionRequestUpdate.getIncrementPrice());
        auctionRequest.setMethodType(auctionRequestUpdate.getMethodType());
        auctionRequest.setRequestCreatedDate(LocalDateTime.now());
        auctionRequest.setRequestStatus(KoiStatus.PENDING_APPROVAL.ordinal());

        auctionRequestRepository.save(auctionRequest);

        AuctionRequestResponse response = new AuctionRequestResponse();
        response.setId(auctionRequest.getId());
        response.setBreeder(auctionRequest.getBreeder());
        response.setFish(auctionRequest.getFish());
        response.setMethodType(auctionRequest.getMethodType());

        return response;

    }

    @Override
    public AuctionResponse approveRequestUpdateAuction(int auctionRequestId, int staffId) {

        swp.auctionkoi.models.AuctionRequest auctionRequest = auctionRequestRepository.findById(auctionRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_REQUEST_NOT_EXISTED));

        if (!auctionRequest.getRequestStatus().equals(KoiStatus.PENDING_APPROVAL)) {
            throw new AppException(ErrorCode.AUCTION_NOT_APPROVE);
        }

        Auction auction = auctionRepository.findById(auctionRequest.getId())
                .orElseThrow(() -> new AppException(ErrorCode.AUCTION_NOT_EXISTED));

        auction.setId(auctionRequest.getAuction().getId());
        auction.setFish(auctionRequest.getFish());

        auctionRequest.setRequestStatus(KoiStatus.APPROVED.ordinal());
        auctionRequest.setRequestUpdatedDate(LocalDateTime.now());

        auctionRepository.save(auction);
        auctionRequestRepository.save(auctionRequest);

        AuctionResponse response = new AuctionResponse();
        response.setAuctionId(auction.getId());
        response.setStartTime(auction.getStartTime());
        response.setEndTime(auction.getEndTime());
        response.setCurrentPrice(auction.getCurrentPrice());
        response.setStatus(auction.getStatus().toString());

        return response;
    }

}
