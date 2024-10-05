package swp.auctionkoi.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuctionRequestDTO {
//    Integer id;
//    swp.auctionkoi.models.User breeder;
//    swp.auctionkoi.models.KoiFish fish;
//    swp.auctionkoi.models.Auction auction;
    Integer breederId;
    Integer fishId;
    Integer auctionId;
    Double buyOut;
    Double startPrice;
    Double incrementPrice;
    Integer methodType;
//    LocalDate requestCreatedDate;
//    LocalDate requestUpdatedDate;
    Integer requestStatus;
}
