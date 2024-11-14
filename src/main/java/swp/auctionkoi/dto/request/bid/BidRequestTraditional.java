package swp.auctionkoi.dto.request.bid;

import lombok.*;
import lombok.experimental.FieldDefaults;
import swp.auctionkoi.models.enums.AutoBidIncrement;

import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BidRequestTraditional {
    int auctionId;
    int userId;
    float bidAmount;
    boolean isAutoBid;
//    AutoBidIncrement incrementAutobid;
    float maxBidAmount;
}
