package swp.auctionkoi.dto.respone;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuctionResponse {
    private String status;
    private String message;
    private Integer auctionId;
    private Double currentPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
