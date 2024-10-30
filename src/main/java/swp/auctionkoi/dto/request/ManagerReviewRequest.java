package swp.auctionkoi.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ManagerReviewRequest {
     int auctionRequestId;
     int managerId;
     int staffId;
     @JsonProperty("isApproved")
     boolean isApproved;
     @JsonProperty("assignToStaff")
     boolean assignToStaff;
}
