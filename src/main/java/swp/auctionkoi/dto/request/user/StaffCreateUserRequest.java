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
public class StaffCreateUserRequest {
    String username;
    String password;
    String fullname;
    String phone;
    String address;
    String avatar_url;
    boolean isBreeder;
}
