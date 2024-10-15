package swp.auctionkoi.dto.request;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuctionDTO {
    Integer AuctionId;
    Integer fishId;
    LocalDateTime startTime;
    LocalDateTime endTime;
    Double currentPrice;
    Integer status;
}
