package swp.auctionkoi.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuctionRequest {
//    Integer id;
//    swp.auctionkoi.models.User breeder;
//    swp.auctionkoi.models.KoiFish fish;
//    swp.auctionkoi.models.Auction auction;
    Integer breederId;
    Integer fishId;
//    List<String> imageUrls;
//    List<String> videoUrls;
    Integer auctionId;
    Double buyOut;
    Double startPrice;
    Double incrementPrice;
    Integer methodType;

//    LocalDateTime Start_time;
//    LocalDateTime End_time;
//    LocalDate requestCreatedDate;
//    LocalDate requestUpdatedDate;
    Integer requestStatus;


}
