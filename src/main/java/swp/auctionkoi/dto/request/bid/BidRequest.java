package swp.auctionkoi.dto.request.bid;

import lombok.*;
import lombok.experimental.FieldDefaults;
import swp.auctionkoi.models.enums.AutoBidIncrement;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BidRequest {
    int auctionId;
    float bidAmount;
}
