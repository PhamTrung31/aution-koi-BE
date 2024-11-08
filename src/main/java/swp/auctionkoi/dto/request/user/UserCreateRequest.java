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
    @Size(min = 5, message = "Invalid username!")
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
    @NotBlank(message = "Avatar URL cannot be blank!")
    String avatar_url;
}
