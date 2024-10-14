package swp.auctionkoi.service.koi_fish_image.impl;

import org.springframework.stereotype.Service;
import swp.auctionkoi.models.KoiFishImage;
import swp.auctionkoi.service.koi_fish_image.KoiFishImageService;

import java.util.List;

@Service
public class KoiFishImageServiceImpl implements KoiFishImageService {
    @Override
    public KoiFishImage addKoiFishImage(KoiFishImage koiFishImage) {
        return null;
    }

    @Override
    public List<KoiFishImage> getListImageByKoiFishId(int koiFishId) {
        return List.of();
    }

    @Override
    public List<KoiFishImage> getKoiFishImageById(int koiFishImageId) {
        return List.of();
    }
}
