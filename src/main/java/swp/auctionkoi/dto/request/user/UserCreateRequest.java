package swp.auctionkoi.dto.request.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateRequest {
    int id;
    String username;
    String password;
    String fullname;
    String phone;
    String address;
    String avatar_url;
}
