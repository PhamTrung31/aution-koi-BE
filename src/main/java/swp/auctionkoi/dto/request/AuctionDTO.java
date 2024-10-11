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
    Integer id;
    Integer breederId;
    Integer fishId;
    Integer auctionRequestId;
    Double buyOut;
    Double startPrice;
    Double incrementPrice;
    Integer methodType;
//    Integer requestStatus;
//    List<String> imageUrls;
//    List<String> videoUrls;

    LocalDateTime startTime;
    LocalDateTime endTime;
    Double currentPrice;
    Integer status;
}
