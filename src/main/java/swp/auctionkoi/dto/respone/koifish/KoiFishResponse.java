package swp.auctionkoi.dto.respone.koifish;

import lombok.*;
import lombok.experimental.FieldDefaults;
import swp.auctionkoi.models.enums.KoiStatus;
import swp.auctionkoi.models.enums.Sex;

@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KoiFishResponse {
    String name;
    Sex sex;
    Double size;
    Integer age;
    String description;
//    String image;
    KoiStatus status;

}
