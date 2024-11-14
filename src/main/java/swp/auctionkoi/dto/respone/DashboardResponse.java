package swp.auctionkoi.dto.respone;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DashboardResponse {
    private int totalBreeders;
    private int totalMembers;
    private int totalAuctions;
    private float profit;
}
