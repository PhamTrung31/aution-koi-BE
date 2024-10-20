package swp.auctionkoi.dto.request.user;

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
    Boolean isActive;
    Boolean isBreeder;
}
