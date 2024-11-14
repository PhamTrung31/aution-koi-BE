package swp.auctionkoi.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Username cannot be blank!")
    String username;
    @NotBlank(message = "Password cannot be blank!")
    String password;
    @NotBlank(message = "Full name cannot be blank!")
    String fullname;
    @NotBlank(message = "Phone number cannot be blank!")
    @Pattern(regexp = "^0\\d{9}$", message = "Phone number must start with 0 and be exactly 10 digits")
    String phone;
    @NotBlank(message = "Address cannot be blank!")
    String address;
    String avatar_url;
}
