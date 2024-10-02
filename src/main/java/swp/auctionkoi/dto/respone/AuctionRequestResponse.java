package swp.auctionkoi.dto.respone;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuctionRequestResponse {
    private String status;
    private String message;
    private Integer id;
    private swp.auctionkoi.models.User breeder;
    private swp.auctionkoi.models.KoiFish fish;
    private swp.auctionkoi.models.Auction auction;
    private Double buyOut;
    private Double startPrice;
    private Double incrementPrice;

    private Integer methodType;

}
