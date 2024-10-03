package swp.auctionkoi.service.user.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.request.UserCreateRequest;
import swp.auctionkoi.dto.request.UserUpdateRequest;
import swp.auctionkoi.dto.respone.UserResponse;
import swp.auctionkoi.models.User;
import swp.auctionkoi.models.enums.Role;
import swp.auctionkoi.repository.UserRepository;
import swp.auctionkoi.service.user.ManagerService;

import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ManagerServiceImpl implements ManagerService {


    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    @Override
    public HashMap<Integer, User> getAllStaff() {
        return null;
    }

    @Override
    public Optional<UserResponse> getStaff(int id) {
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
    public Optional<UserResponse> updateStaff(int id, UserUpdateRequest user) {
        return Optional.empty();
    }

    @Override
    public boolean deleteStaff(int id) {
        return false;
    }

}
