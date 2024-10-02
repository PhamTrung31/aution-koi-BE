package swp.auctionkoi.service.user;

import swp.auctionkoi.dto.request.UserCreateRequest;
import swp.auctionkoi.models.User;

public interface KoiBreederService {
    public User createAccountBreeder(UserCreateRequest request);
}
