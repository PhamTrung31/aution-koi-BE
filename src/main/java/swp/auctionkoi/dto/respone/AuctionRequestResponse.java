package swp.auctionkoi.dto.respone;

import lombok.*;
import swp.auctionkoi.models.AuctionRequest;
import swp.auctionkoi.models.enums.AuctionType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class AuctionRequestResponse {
    private boolean success;
    private String message;
    private AuctionRequest data;
}
