package swp.auctionkoi.dto.request.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StaffUpdateUserRequest {
    int userId;
    String password;
    String fullname;
    String phone;
    String address;
    String avatar_url;
    @JsonProperty("isBreeder")
    Boolean isBreeder;
}
