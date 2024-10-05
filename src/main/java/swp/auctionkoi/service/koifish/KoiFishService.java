package swp.auctionkoi.service.koifish;

import swp.auctionkoi.dto.request.koifish.KoiFishCreateRequest;
import swp.auctionkoi.dto.request.koifish.KoiFishUpdateRequest;
import swp.auctionkoi.dto.respone.koifish.KoiFishResponse;

import java.util.HashMap;
import java.util.Optional;

public interface KoiFishService {
    public HashMap<Integer, KoiFishResponse> getAllFish();
    public Optional<KoiFishResponse> getFish(int id);
    public KoiFishResponse addFish(KoiFishCreateRequest fish);
    public Optional<KoiFishResponse> updateFish(KoiFishUpdateRequest fish);
    public boolean deleteFish(int id);
}
