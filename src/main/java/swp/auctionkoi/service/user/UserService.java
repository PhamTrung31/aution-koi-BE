package swp.auctionkoi.service.user;

import swp.auctionkoi.dto.request.user.UserCreateRequest;
import swp.auctionkoi.dto.request.user.UserLoginRequest;
import swp.auctionkoi.dto.respone.user.UserResponse;
import swp.auctionkoi.models.User;

import javax.swing.text.html.Option;
import java.util.HashMap;
import java.util.Optional;

public interface UserService {
    public UserResponse login(UserLoginRequest userLoginRequest);
    public void logout();
    public User create(UserCreateRequest request);
    public UserResponse viewProfile();
    public void updateProfile(User user);
    public HashMap<Integer, User> getAllBreeder();
}
