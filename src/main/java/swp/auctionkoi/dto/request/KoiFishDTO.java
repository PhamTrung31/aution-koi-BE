package swp.auctionkoi.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import swp.auctionkoi.models.KoiFishs;
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
    Float size;
    Integer age;
    String description;
    String image_Url;
    String video_Url;
    KoiStatus status;

    public KoiFishDTO(@NotNull KoiFishs fish) {
        this.breederId = fish.getBreeder().getId();  // Lấy thông tin breeder
        this.name = fish.getName();
        this.sex = fish.getSex();
        this.size = fish.getSize();
        this.age = fish.getAge();
        this.description = fish.getDescription();
        this.image_Url = fish.getImageUrl();
        this.video_Url = fish.getVideoUrl();
        this.status = fish.getStatus();
    }

    public static KoiFishDTO fromKoiFish(KoiFishs fish) {
        if (fish == null) {
            return null;
        }
        return new KoiFishDTO(
                fish.getId(),
                fish.getName(),
                fish.getSex(),
                fish.getSize(),
                fish.getAge(),
                fish.getDescription(),
                fish.getImageUrl(),
                fish.getVideoUrl(),
                fish.getStatus()

        );
    }

}
