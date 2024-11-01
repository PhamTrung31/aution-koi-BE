package swp.auctionkoi.service.koifish;

import swp.auctionkoi.dto.request.koifish.KoiFishUpdateRequest;
import swp.auctionkoi.models.KoiFishs;

import java.util.List;

public interface KoiFishService {
    public KoiFishs getKoiFishById(int id);
    public KoiFishs createKoiFish(KoiFishs koiFish, int breederId);
    public KoiFishs updateKoiFish(int id, KoiFishUpdateRequest updatedKoiFish);
    public List<KoiFishs> viewKoiFishByBreederId(int breederid);
    public KoiFishs cancelKoiFish(int id);



}
