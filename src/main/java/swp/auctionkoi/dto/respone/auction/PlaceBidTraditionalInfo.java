package swp.auctionkoi.dto.respone.auction;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlaceBidTraditionalInfo {
    int winner_Id;
    float highest_price;
    Instant end_time;
    String winner_fullname;
}
