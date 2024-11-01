package swp.auctionkoi.dto.respone.auction;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuctionResonpse {
    Integer auction_id;
    Integer fish_id;
}
