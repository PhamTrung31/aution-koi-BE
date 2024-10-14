package swp.auctionkoi.service.koi_fish_image;


import swp.auctionkoi.models.KoiFishImage;

import java.util.List;

public interface KoiFishImageService {
    public KoiFishImage addKoiFishImage(KoiFishImage koiFishImage);
    List<KoiFishImage> getListImageByKoiFishId(int koiFishId);
    public List<KoiFishImage> getKoiFishImageById(int koiFishImageId);
}
