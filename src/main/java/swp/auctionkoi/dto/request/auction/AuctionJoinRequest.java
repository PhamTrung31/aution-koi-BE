package swp.auctionkoi.dto.request.auction;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuctionJoinRequest {
    int userId;
    int auctionId;
}
