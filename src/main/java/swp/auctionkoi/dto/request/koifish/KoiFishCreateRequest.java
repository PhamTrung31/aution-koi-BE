package swp.auctionkoi.dto.request.koifish;

import swp.auctionkoi.models.enums.KoiStatus;
import swp.auctionkoi.models.enums.Sex;

public class KoiFishCreateRequest {
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
