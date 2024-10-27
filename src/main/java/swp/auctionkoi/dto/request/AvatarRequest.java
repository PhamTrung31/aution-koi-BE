package swp.auctionkoi.dto.request;

import com.google.firebase.database.annotations.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AvatarRequest {

    @NotNull
    @URL(message = "Invalid URL format")
    private String avatarUrl;
}
