package swp.auctionkoi.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

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
    LocalDate requestCreatedDate;
    LocalDate requestUpdatedDate;
    Integer requestStatus;
}
