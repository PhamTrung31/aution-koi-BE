package swp.auctionkoi.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuctionRequestUpdate {
    Integer id;
    swp.auctionkoi.models.User breeder;
    swp.auctionkoi.models.KoiFish fish;
    swp.auctionkoi.models.Auction auction;
    Double buyOut;
    Double startPrice;
    Double incrementPrice;
    Integer methodType;
    LocalDateTime requestCreatedDate;
    LocalDateTime requestUpdatedDate;
    LocalDateTime requestStartDate;
    LocalDateTime requestEndDate;
    Integer requestStatus;
}
