package swp.auctionkoi.service.user;

import swp.auctionkoi.dto.request.user.UserCreateRequest;
import swp.auctionkoi.dto.request.user.UserUpdateRequest;
import swp.auctionkoi.dto.respone.user.UserResponse;
import swp.auctionkoi.models.User;

import java.util.HashMap;
import java.util.Optional;

public interface ManagerService {
    public HashMap<Integer, User> getAllStaff();
    public UserResponse getStaff(int id);
    public User addStaff(UserCreateRequest request);
    public UserResponse updateStaff(int id, UserUpdateRequest user);
    public boolean deleteStaff(int id);
}
