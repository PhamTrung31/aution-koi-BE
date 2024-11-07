package swp.auctionkoi.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "Buy out cannot be null!")
    @DecimalMin(value = "0.0", inclusive = true, message = "Buy out must be a positive number")
    Float buyOut;
    //    Integer incrementStep;
    @NotNull(message = "Start price cannot be null!")
    @DecimalMin(value = "0.0", inclusive = true, message = "Start price must be a positive number")
    Float startPrice;
    AuctionType methodType;
//    Instant start_time;
//    Instant end_time;
}
