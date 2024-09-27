package swp.auctionkoi.service.user;

import swp.auctionkoi.dto.request.UserCreateRequest;
import swp.auctionkoi.dto.request.UserUpdateRequest;
import swp.auctionkoi.dto.respone.UserResponse;
import swp.auctionkoi.models.Auction;
import swp.auctionkoi.models.Bid;
import swp.auctionkoi.models.User;

import java.util.HashMap;
import java.util.Optional;

public interface StaffService {
    public HashMap<Integer, User> getAllUser();
    public Optional<UserResponse> getUser(int id);
    public Optional<User> addUser(UserCreateRequest request);
    public Optional<UserResponse> updateUser(int id, UserUpdateRequest user);
    public boolean deleteUser(int id);

    public Optional<Auction> getAuction(int id);
    public Auction addAuction(Auction auction);
    public Auction updateAuction(Auction auction);
    public void deleteAuction(int id);

    public void viewDetailAuction(int auctionId);
    public HashMap<Integer, Bid> viewHistoryBidAuction(int auctionId);
}
