package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import swp.auctionkoi.dto.request.AuctionRequestResponseData;
import swp.auctionkoi.dto.respone.AuctionRequestResponse;
import swp.auctionkoi.dto.respone.AuctionRequestUpdateResponse;
import swp.auctionkoi.models.AuctionRequest;
import swp.auctionkoi.models.enums.AuctionRequestStatus;
import swp.auctionkoi.models.enums.AuctionType;

import javax.swing.text.html.Option;
import java.time.Instant;
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

    @Query("SELECT ar FROM AuctionRequest ar WHERE ar.assignedStaff.id = :staffId AND ar.requestStatus = :status")
    List<AuctionRequest> findByAssignedStaffIdAndStatus(Integer staffId, AuctionRequestStatus status);

    @Query("SELECT ar FROM AuctionRequest ar WHERE ar.assignedStaff.id = :staffId")
    List<AuctionRequest> findByAssignedStaffId(Integer staffId);

    @Query("SELECT ar FROM AuctionRequest ar WHERE ar.endTime BETWEEN :startTimeMinusOneHour AND :startTime")
    List<AuctionRequest> findCloseEndTimes(@Param("startTimeMinusOneHour") Instant startTimeMinusOneHour, @Param("startTime") Instant startTime);

    @Query("SELECT COUNT(ar) FROM AuctionRequest ar JOIN ar.auction a WHERE YEAR(ar.endTime) = :year " +
            "AND MONTH(ar.endTime) = :month AND a.status = swp.auctionkoi.models.enums.AuctionStatus.COMPLETED")
    int countCompletedAuctionsByEndTimeYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT COUNT(ar) FROM AuctionRequest ar WHERE ar.methodType = :type")
    int countAuctionsByType(@Param("type") AuctionType type);
}
