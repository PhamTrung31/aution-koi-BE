package swp.auctionkoi.dto.respone.auction;

import lombok.*;
import lombok.experimental.FieldDefaults;
import swp.auctionkoi.models.enums.AuctionType;
import swp.auctionkoi.models.enums.Sex;

import java.lang.invoke.MethodType;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuctionPendingInfo {
    Integer auction;
    Integer auction_id;
    Integer fish_id;
    String fish_name;
    Integer fish_age;
    Float fish_size;
    Sex fish_sex;
    String imageUrl;
    String videoUrl;
    Float deposit_amount;
    AuctionType methodType;
    Instant start_time;
    Instant end_time;
}
