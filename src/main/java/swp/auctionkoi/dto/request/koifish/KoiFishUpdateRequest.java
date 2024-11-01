package swp.auctionkoi.dto.request.koifish;

import lombok.*;
import lombok.experimental.FieldDefaults;
import swp.auctionkoi.models.enums.Sex;

@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KoiFishUpdateRequest {
    String name;
    Sex sex;
    Double size;
    Integer age;
    String description;
    String imageUrl;
    String videoUrl;

}
