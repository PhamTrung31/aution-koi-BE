package swp.auctionkoi.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import swp.auctionkoi.models.User;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO  {
     Integer id;
     String fullname;
     String username;
     String phone;
     String avatarUrl;
     String role;

    public UserDTO(User user) {
        this.id = user.getId();
        this.fullname = user.getFullname();
        this.username = user.getUsername();
        this.phone = user.getPhone();
        this.avatarUrl = user.getAvatarUrl();
        this.role = user.getRole().toString();
    }
    public static UserDTO fromUser(User user) {
        if (user == null) {
            return null;
        }
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getFullname(),
                user.getPhone(),
                user.getAddress(),
                user.getAvatarUrl()
        );

    }

}
