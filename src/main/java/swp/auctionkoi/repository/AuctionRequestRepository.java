package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.auctionkoi.dto.request.AuctionRequestResponseData;
import swp.auctionkoi.dto.respone.AuctionRequestResponse;
import swp.auctionkoi.dto.respone.AuctionRequestUpdateResponse;
import swp.auctionkoi.models.AuctionRequest;
import swp.auctionkoi.models.enums.AuctionRequestStatus;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import java.util.Optional;

public interface AuctionRequestRepository extends JpaRepository<AuctionRequest, Integer> {
    Optional<AuctionRequest> findByAuctionId(int auctionId);
//    AuctionRequest findByAuctionIdForLeaderBoard(int auctionId);
    Optional<AuctionRequest> findById(int auctionId);

    List<AuctionRequest> findListAuctionRequestByUserId(int userId); //breeder

    List<AuctionRequest> findByAuctionIsNotNull();

    boolean existsAuctionsByStartTime(LocalDateTime auctionDateTime);

    List<AuctionRequest> findByRequestStatus(AuctionRequestStatus requestStatus);
}
