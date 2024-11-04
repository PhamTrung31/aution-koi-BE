package swp.auctionkoi.dto.respone.auction;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuctionHistoryResponse {
    Integer auction_id;
    Integer fish_id;
    Instant start_time;
    Instant end_time;
}
