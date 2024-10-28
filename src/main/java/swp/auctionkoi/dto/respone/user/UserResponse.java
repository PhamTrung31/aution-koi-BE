package swp.auctionkoi.dto.respone.user;

import lombok.*;
import lombok.experimental.FieldDefaults;
import swp.auctionkoi.models.enums.Role;

@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    Integer id;
    String username;
    String fullname;
    String phone;
    String address;
    Role role;
}
