package swp.auctionkoi.service.user.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.respone.ApiResponse;
import swp.auctionkoi.dto.request.user.UserCreateRequest;
import swp.auctionkoi.dto.request.user.UserUpdateRequest;
import swp.auctionkoi.dto.respone.user.UserResponse;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.mapper.UserMapper;
import swp.auctionkoi.models.User;
import swp.auctionkoi.models.enums.Role;
import swp.auctionkoi.repository.UserRepository;
import swp.auctionkoi.service.user.ManagerService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasAuthority('ROLE_MANAGER')")
public class ManagerServiceImpl implements ManagerService {

    UserMapper userMapper;

    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

//    @Override
//    public HashMap<Integer, User> getAllStaff() {
//
//        HashMap<Integer, User> staff = new HashMap<>();
//        List<User> users = userRepository.findAll();
//        for (User user : users) {
//            if (user.getRole() == Role.STAFF)
//                staff.put(user.getId(), user);
//        }
//        return staff;
//    }
public List<User> getAllStaff() {
    List<User> staffList = new ArrayList<>();
    List<User> users = userRepository.findAll();
    for (User user : users) {
        if (user.getRole() == Role.STAFF) {
            staffList.add(user);
        }
    }
    return staffList;
}

    @Override
    public UserResponse getStaff(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));
        if (user.getRole() == Role.STAFF) {
            UserResponse userResponse = new UserResponse();
            return userResponse;
        }
        throw new AppException(ErrorCode.STAFF_NOT_FOUND);
    }


    @Override
    public User addStaff(UserCreateRequest request) {
        // Check for existing username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.STAFF_EXISTED);
        }

//        User user = new User();

        User user = userMapper.toUser(request);

//         Encode the password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Set the role for the user
        user.setRole(Role.STAFF);

        // Save and return the new user
        return userRepository.save(user);
    }


    @Override
    public UserResponse updateStaff(int id, UserUpdateRequest user) {
        User user1 = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));
        if (user != null && user1.getRole() == Role.STAFF) {
            userMapper.updateStaff(user1, user);
            userRepository.save(user1);
            UserResponse userResponse = new UserResponse();
            return userResponse;
        }
        throw new AppException(ErrorCode.STAFF_NOT_FOUND);
    }

    @Override
    public boolean deleteStaff(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));
        if (user.getRole() != Role.STAFF) {
            throw new AppException(ErrorCode.STAFF_NOT_FOUND);
        }

        userRepository.delete(user);
        return true;

    }

}
