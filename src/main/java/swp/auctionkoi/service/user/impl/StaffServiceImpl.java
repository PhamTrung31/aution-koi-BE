package swp.auctionkoi.service.user.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.request.user.StaffCreateUserRequest;
import swp.auctionkoi.dto.request.user.StaffUpdateUserRequest;
import swp.auctionkoi.dto.request.user.UserCreateRequest;
import swp.auctionkoi.dto.request.user.UserUpdateRequest;
import swp.auctionkoi.dto.respone.user.UserResponse;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.mapper.UserMapper;
import swp.auctionkoi.models.Auction;
import swp.auctionkoi.models.Bid;
import swp.auctionkoi.models.User;
import swp.auctionkoi.models.enums.Role;
import swp.auctionkoi.repository.AuctionRepository;
import swp.auctionkoi.repository.UserRepository;
import swp.auctionkoi.service.user.StaffService;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('STAFF')")
public class StaffServiceImpl implements StaffService {

    UserRepository userRepository;

    AuctionRepository auctionRepository;

    UserMapper userMapper;

    PasswordEncoder passwordEncoder;

    @Override
    public HashMap<Integer, UserResponse> getAllUser() {
        HashMap<Integer, UserResponse> users = new HashMap<>();
        List<User> userList = userRepository.findAll();
        for (User user : userList) {
            if(user.getRole() == Role.BREEDER || user.getRole() == Role.MEMBER)
            users.put(user.getId(), userMapper.toUserResponse(user));
        }
        return users;
    }



    @Override
    public UserResponse getUserById(int id) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

    @Override
    public User addUser(StaffCreateUserRequest request) {

        if(userRepository.existsByUsername(request.getUsername()))
            throw new RuntimeException("Username already exists");

        request.setPassword(passwordEncoder.encode(request.getPassword()));

        User user = userMapper.toUser(request);

        log.info("Request field isBreeder: " + request.isBreeder());

        if(request.isBreeder()){
            user.setRole(Role.BREEDER);
        } else{
            user.setRole(Role.MEMBER);
        }

        log.info("User role: " + user.getRole());

        user.setIsActive(true);

        userRepository.save(user);

        return userMapper.toUser(user);
    }

    @Override
    public UserResponse updateUser(StaffUpdateUserRequest updateUser) {
        User user = userRepository.findById(updateUser.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
        if(updateUser.getPassword() != null){
            updateUser.setPassword(passwordEncoder.encode(updateUser.getPassword()));
        } else {
            updateUser.setPassword(user.getPassword());
        }
        userMapper.updateUser(user, updateUser);
        if(updateUser.getIsBreeder()){
            user.setRole(Role.BREEDER);
        } else {
            user.setRole(Role.MEMBER);
        }

        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    @Override
    public boolean deleteUser(int id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.deleteById(user.getId());
        return true;
    }

    @Override
    public void banUser(int userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    public void unBanUser(int userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(true);
        userRepository.save(user);
    }

}
