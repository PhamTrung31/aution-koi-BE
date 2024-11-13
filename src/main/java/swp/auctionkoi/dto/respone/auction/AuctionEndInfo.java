package swp.auctionkoi.dto.respone.auction;

import lombok.*;
import lombok.experimental.FieldDefaults;
import swp.auctionkoi.models.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuctionEndInfo {
    Integer auction_id;
    Integer user_id; //winner
    Float highest_prices;
    String user_fullname;
}
