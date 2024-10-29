package swp.auctionkoi.dto.respone;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import swp.auctionkoi.dto.request.KoiFishDTO;
import swp.auctionkoi.dto.request.UserDTO;
import swp.auctionkoi.models.enums.AuctionRequestStatus;
import swp.auctionkoi.models.enums.AuctionType;

import java.time.Instant;


@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuctionRequestUpdateResponse {
     Integer auctionRequestId;
     UserDTO user;
     Integer approvedStaffId;  // Chỉ lưu trữ ID của Staff
     String requestStatus;
     KoiFishDTO fish;
     Float buyOut;
     Float startPrice;
     AuctionType methodType;
     Instant requestCreatedDate;
     Instant requestUpdatedDate;
     Instant startTime;
     Instant endTime;

     // Constructor chuyển đổi từ entity và các DTO
     public AuctionRequestUpdateResponse(
             Integer auctionRequestId,
             UserDTO user,
             Integer approvedStaffId,  // Chỉ lấy ID của Staff
             @NotNull AuctionRequestStatus requestStatus,
             KoiFishDTO fish,
             @NotNull Float buyOut,
             @NotNull Float startPrice,
             @NotNull AuctionType methodType,
             Instant requestCreatedDate,
             Instant requestUpdatedDate,
             @NotNull Instant startTime,
             @NotNull Instant endTime
     ) {
          this.auctionRequestId = auctionRequestId;
          this.user = user;
          this.approvedStaffId = approvedStaffId;  // Lưu ID của Staff
          this.requestStatus = requestStatus.name();  // Chuyển từ enum sang String
          this.fish = fish;
          this.buyOut = buyOut;
          this.startPrice = startPrice;
          this.methodType = methodType;
          this.requestCreatedDate = requestCreatedDate;
          this.requestUpdatedDate = requestUpdatedDate;
          this.startTime = startTime;
          this.endTime = endTime;
     }
}