package swp.auctionkoi.service.user;

import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.request.UserCreateRequest;
import swp.auctionkoi.models.User;

public interface UserService {
    public void login();
    public void logout();
    public User createAccount(UserCreateRequest request);
    public void viewProfile(int userId);
    public void updateProfile(User user);
}
