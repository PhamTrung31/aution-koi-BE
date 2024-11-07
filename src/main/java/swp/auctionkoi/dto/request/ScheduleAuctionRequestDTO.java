package swp.auctionkoi.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import lombok.*;
import lombok.experimental.FieldDefaults;
import shaded_package.javax.validation.constraints.NotNull;

import java.time.Instant;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduleAuctionRequestDTO {
    Integer auctionRequestId;
    Integer staffId;
    Integer incrementStep;
    @JsonProperty("start_time")
    Instant startTime;
    @JsonProperty("end_time")
    Instant endTime;
}
