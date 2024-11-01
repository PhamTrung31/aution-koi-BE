package swp.auctionkoi.service.bid.impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.*;
import swp.auctionkoi.models.enums.AuctionRequestStatus;
import swp.auctionkoi.models.enums.AuctionStatus;
import swp.auctionkoi.models.enums.AuctionType;
import swp.auctionkoi.repository.*;
import swp.auctionkoi.service.bid.BidService;
import swp.auctionkoi.service.transaction.TransactionService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class BidServiceImpl implements BidService {


    BidRepository bidRepository;

    @Override
    public List<Bid> getAllBidsInAuction(Integer auctionId) {
        return bidRepository.getListBidByAuctionId(auctionId);
    }

    @Override
    public List<Bid> getTop5BidsInAuction(Integer auctionId) {
        return bidRepository.findTop5HighestBidsByAuctionId(auctionId);
    }
}
