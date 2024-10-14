package swp.auctionkoi.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import swp.auctionkoi.models.enums.KoiStatus;
import swp.auctionkoi.models.enums.Sex;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KoiFishDTO {
    Integer breederId;
    String name;
    Sex sex;
    Double size;
    Integer age;
    String description;
    String image_Url;
    String video_Url;
    KoiStatus status;
}
