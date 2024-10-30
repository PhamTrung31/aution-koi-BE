package swp.auctionkoi.dto.respone.auction;

import lombok.*;
import lombok.experimental.FieldDefaults;
import swp.auctionkoi.models.KoiFish;
import swp.auctionkoi.models.enums.AuctionStatus;
import swp.auctionkoi.models.enums.AuctionType;
import swp.auctionkoi.models.enums.Sex;

import javax.swing.*;
import java.lang.invoke.MethodType;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuctionStartInfo {
    Integer auction_id;
    Integer fish_id;
    String fish_name;
    Integer fish_age;
    Float fish_size;
    Sex fish_sex;
    String imageUrl;
    String videoUrl;
    AuctionStatus aution_status;
    Float deposit_amount;
    Instant start_time;
    Instant end_time;
    AuctionType method_type;
    Float buy_out;
}
