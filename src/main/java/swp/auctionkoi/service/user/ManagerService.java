package swp.auctionkoi.service.user;

import swp.auctionkoi.dto.request.UserCreateRequest;
import swp.auctionkoi.dto.request.UserUpdateRequest;
import swp.auctionkoi.dto.respone.UserResponse;
import swp.auctionkoi.models.User;

import java.util.HashMap;
import java.util.Optional;

public interface ManagerService {
    public HashMap<Integer, User> getAllStaff();
    public Optional<UserResponse> getStaff(int id);
    public Optional<User> addStaff(UserCreateRequest request);

    public Optional<UserResponse> updateActiveStaff(int id, UserUpdateRequest user);

    public boolean deleteStaff(int id);
}
