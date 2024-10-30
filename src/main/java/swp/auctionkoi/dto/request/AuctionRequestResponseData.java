package swp.auctionkoi.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import swp.auctionkoi.models.KoiFish;
import swp.auctionkoi.models.User;
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
public class AuctionRequestResponseData {
    int id;
    User user;
    KoiFish fish;
    Float buyOut;
    Float startPrice;
    Integer incrementStep;
    Float incrementPrice;
    AuctionType methodType;
    Instant requestCreatedDate;
    Instant requestUpdatedDate;
    AuctionRequestStatus requestStatus;
}
