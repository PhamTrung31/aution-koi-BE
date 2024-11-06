package swp.auctionkoi.service.koifish;

import swp.auctionkoi.dto.request.koifish.KoiFishUpdateRequest;
import swp.auctionkoi.models.KoiFish;
import swp.auctionkoi.models.enums.KoiStatus;

import java.util.List;

public interface KoiFishService {
    public KoiFish getKoiFishById(int id);
    public KoiFish createKoiFish(KoiFish koiFish, int breederId);
    public KoiFish updateKoiFish(int id, KoiFishUpdateRequest updatedKoiFish);
    public List<KoiFish> viewKoiFishByBreederIdInNew(int breederId);
    public KoiFish cancelKoiFish(int id);



}
