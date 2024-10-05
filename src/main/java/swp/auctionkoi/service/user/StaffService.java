package swp.auctionkoi.service.user;

import swp.auctionkoi.dto.request.user.UserCreateRequest;
import swp.auctionkoi.dto.request.user.UserUpdateRequest;
import swp.auctionkoi.dto.respone.user.UserResponse;
import swp.auctionkoi.service.auction.AuctionService;

import java.util.HashMap;
import java.util.Optional;

public interface StaffService extends AuctionService {
    public HashMap<Integer, UserResponse> getAllUser();
    public Optional<UserResponse> getUser(int id);
    public Optional<UserResponse> addUser(UserCreateRequest request);
    public Optional<UserResponse> updateUser(int id, UserUpdateRequest user);
    public boolean deleteUser(int id);
}
