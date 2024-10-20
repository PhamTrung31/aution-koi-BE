package swp.auctionkoi.service.user;

import swp.auctionkoi.dto.request.user.StaffCreateUserRequest;
import swp.auctionkoi.dto.request.user.StaffUpdateUserRequest;
import swp.auctionkoi.dto.request.user.UserCreateRequest;
import swp.auctionkoi.dto.request.user.UserUpdateRequest;
import swp.auctionkoi.dto.respone.user.UserResponse;
import swp.auctionkoi.service.auction.AuctionService;

import java.util.HashMap;
import java.util.Optional;

public interface StaffService {
    public HashMap<Integer, UserResponse> getAllUser();
    public UserResponse getUserById(int id);
    public UserResponse addUser(StaffCreateUserRequest request);
    public UserResponse updateUser(StaffUpdateUserRequest user);
    public boolean deleteUser(int id);
    public void banUser(int userId);
    public void unBanUser(int userId);
}
