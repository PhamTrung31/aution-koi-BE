package swp.auctionkoi.service.user;

import swp.auctionkoi.dto.request.user.UserCreateRequest;
import swp.auctionkoi.dto.respone.user.UserResponse;
import swp.auctionkoi.models.User;

import java.util.HashMap;

public interface UserService {
    public void login();
    public void logout();
    public User create(UserCreateRequest request);
    public UserResponse viewProfile();
    public void updateProfile(User user);
    public HashMap<Integer, User> getAllBreeder();
    public void updateAvatar(int id, String avatarUrl);
}
