package swp.auctionkoi.service.user.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.request.ApiResponse;
import swp.auctionkoi.dto.request.UserCreateRequest;
import swp.auctionkoi.dto.request.UserUpdateRequest;
import swp.auctionkoi.dto.respone.UserResponse;
import swp.auctionkoi.models.User;
import swp.auctionkoi.models.enums.Role;
import swp.auctionkoi.repository.UserRepository;
import swp.auctionkoi.service.user.ManagerService;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ManagerServiceImpl implements ManagerService {


    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    @Override
    public HashMap<Integer, User> getAllStaff() {

        HashMap<Integer, User> staff = new HashMap<>();
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getRole() == Role.STAFF)
                staff.put(user.getId(), user);
        }
        return staff;
    }

    @Override
    public Optional<UserResponse> getStaff(int id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null && user.getRole() == Role.STAFF) {
            UserResponse userResponse = new UserResponse();
            return Optional.of(userResponse);
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> addStaff(UserCreateRequest request) {
        // Check for existing username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setFullname(request.getFullname());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());

        // Encode the password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Set the role for the user
        user.setRole(Role.STAFF);

        // Save and return the new user
        return Optional.of(userRepository.save(user));
    }


    @Override
    public Optional<UserResponse> updateActiveStaff(int id, UserUpdateRequest user) {
        User user1 = userRepository.findById(id).orElse(null);
        if (user != null && user1.getRole() == Role.STAFF) {
            user1.setIsActive(user.getIsActive());
            userRepository.save(user1);
            UserResponse userResponse = new UserResponse();
            return Optional.of(userResponse);


        }
        return Optional.empty();
    }

    @Override
    public boolean deleteStaff(int id) {
        User user = userRepository.findById(id).get();
        if (user != null && user.getRole() == Role.STAFF) {
            userRepository.delete(user);
            return true;
        }
        return false;
    }

}
