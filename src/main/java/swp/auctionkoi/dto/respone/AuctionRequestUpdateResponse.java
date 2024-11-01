package swp.auctionkoi.dto.respone;

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
     Integer assginedStaffId;
     String requestStatus;
     KoiFishDTO fish;
     Float buyOut;
//     Integer incrementStep;
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
             Integer assginedStaffId,
             @NotNull AuctionRequestStatus requestStatus,
             KoiFishDTO fish,
             @NotNull Float buyOut,
//             @NotNull Integer incrementStep,
             @NotNull Float startPrice,
             @NotNull AuctionType methodType,
             Instant requestCreatedDate,
             Instant requestUpdatedDate,
             @NotNull Instant startTime,
             @NotNull Instant endTime
     ) {
          this.auctionRequestId = auctionRequestId;
          this.user = user;
          this.assginedStaffId = assginedStaffId;
          this.requestStatus = requestStatus.name();  // Chuyển từ enum sang String
          this.fish = fish;
          this.buyOut = buyOut;
//          this.incrementStep = incrementStep;
          this.startPrice = startPrice;
          this.methodType = methodType;
          this.requestCreatedDate = requestCreatedDate;
          this.requestUpdatedDate = requestUpdatedDate;
          this.startTime = startTime;
          this.endTime = endTime;
     }
}
