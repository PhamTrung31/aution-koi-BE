package swp.auctionkoi.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import swp.auctionkoi.models.enums.AuctionType;

import java.time.Instant;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuctionRequestDTO {
    int userId; //breeder
    Integer fishId;
    @JsonProperty("buyOut")
    Float buyOut;
    Float  startPrice;
    AuctionType methodType;
    Instant start_time;
    Instant end_time;
}
