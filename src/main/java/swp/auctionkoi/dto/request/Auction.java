package swp.auctionkoi.dto.request;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Auction {
    Integer AuctionId;
    Integer fishId;
    LocalDateTime startTime;
    LocalDateTime endTime;
    Double currentPrice;
    Integer status;
}
