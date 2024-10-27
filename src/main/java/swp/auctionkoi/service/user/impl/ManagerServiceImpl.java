package swp.auctionkoi.service.user.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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
import java.util.List;

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

        request.setPassword(passwordEncoder.encode(request.getPassword()));

        User user = userMapper.toUser(request);


        user.setIsActive(true);

        // Set the role for the user
        user.setRole(Role.STAFF);

        userRepository.save(user);

        // Save and return the new user
        return userMapper.toUser(user);
    }


    @Override
    public UserResponse updateStaff(int id, UserUpdateRequest user) {
        User user1 = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));
        if (user != null && user1.getRole() == Role.STAFF) {
            if (user.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            } else {
                user.setPassword(user1.getPassword());
            }
            userMapper.updateUser(user1, user);
            userRepository.save(user1);
            return userMapper.toUserResponse(user1);
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

    @Override
    public void banUser(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    public void unBanUser(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(true);
        userRepository.save(user);
    }


}
