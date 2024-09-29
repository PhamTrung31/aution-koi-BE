package swp.auctionkoi.service.user.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.request.ApiResponse;
import swp.auctionkoi.dto.request.UserCreateRequest;
import swp.auctionkoi.dto.request.UserUpdateRequest;
import swp.auctionkoi.dto.respone.UserResponse;
import swp.auctionkoi.mapper.UserMapper;
import swp.auctionkoi.models.Auction;
import swp.auctionkoi.models.Bid;
import swp.auctionkoi.models.User;
import swp.auctionkoi.repository.AuctionRepository;
import swp.auctionkoi.repository.UserRepository;
import swp.auctionkoi.service.user.StaffService;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StaffServiceImpl implements StaffService {

    UserRepository userRepository;

    AuctionRepository auctionRepository;

    UserMapper userMapper;

    @Override
    public HashMap<Integer, User> getAllUser() {
        HashMap<Integer, User> users = new HashMap<>();
        List<User> userList = userRepository.findAll();
        for (User user : userList) {
            users.put(user.getId(), user);
        }
        return users;
    }

    @Override
    public Optional<UserResponse> getUser(int id) {
        User user =  userRepository.findById(id).get();
        return Optional.ofNullable(userMapper.toUserResponse(user));
    }

    @Override
    public Optional<User> addUser(UserCreateRequest request) {
        User user = new User();

        if(userRepository.existsByUsername(request.getUsername()))
            throw new RuntimeException("Username already exists");

        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFullname(request.getFullname());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        return Optional.of(userRepository.save(user));
    }

    @Override
    public Optional<UserResponse> updateUser(int id, UserUpdateRequest tryUpdateUser) {
        User user = userRepository.findById(id).get();
        if(user != null) {
            userMapper.updateUser(user, tryUpdateUser);
            return Optional.of(userMapper.toUserResponse(userRepository.save(user)));
        }
        return null;
    }

    @Override
    public boolean deleteUser(int id) {
        User user = userRepository.findById(id).get();
        if(user != null) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Optional<Auction> getAuction(int id) {

        return null;
    }

    @Override
    public Auction addAuction(Auction auction) {
        return null;
    }

    @Override
    public Auction updateAuction(Auction auction) {
        return null;
    }

    @Override
    public void deleteAuction(int id) {

    }

    @Override
    public void viewDetailAuction(int auctionId) {

    }

    @Override
    public HashMap<Integer, Bid> viewHistoryBidAuction(int auctionId) {
        return null;
    }
}
