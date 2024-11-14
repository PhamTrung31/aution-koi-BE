package swp.auctionkoi.dto.request.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @NotBlank(message = "Username number cannot be blank!")
    String username;
    @NotBlank(message = "Password number cannot be blank!")
    String password;
    @NotBlank(message = "Full name number cannot be blank!")
    String fullname;
    @NotBlank(message = "Phone number cannot be blank!")
    @Pattern(regexp = "^0\\d{9}$", message = "Phone number must start with 0 and be exactly 10 digits")
    String phone;
    @NotBlank(message = "Address number cannot be blank!")
    String address;
    String avatar_url;
    @JsonProperty("isBreeder")
    boolean isBreeder;
}
