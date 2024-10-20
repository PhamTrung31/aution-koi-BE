package swp.auctionkoi.dto.request;

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
    int breederId;
    int fishId;
    float buyOut;
    float startPrice;
    float incrementPrice;
    AuctionType methodType;
    Instant start_time;
    Instant end_time;
}