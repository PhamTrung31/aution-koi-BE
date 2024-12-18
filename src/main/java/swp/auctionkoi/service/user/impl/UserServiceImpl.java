package swp.auctionkoi.service.user.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.request.user.UserCreateRequest;
import swp.auctionkoi.dto.request.user.UserLoginRequest;
import swp.auctionkoi.dto.respone.user.UserResponse;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.mapper.UserMapper;
import swp.auctionkoi.models.User;
import swp.auctionkoi.models.enums.Role;
import swp.auctionkoi.repository.UserRepository;
import swp.auctionkoi.service.user.UserService;

import javax.swing.text.html.Option;
import java.time.Instant;
import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    UserMapper userMapper;

    PasswordEncoder passwordEncoder;

    @Override
    public UserResponse login(UserLoginRequest userLoginRequest) {
        User user = userRepository.findByUsername(userLoginRequest.getUsername()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if(user.getPassword().equals(passwordEncoder.encode(userLoginRequest.getPassword()))){
            UserResponse userResponse = userMapper.toUserResponse(user);
            return userResponse;
        } else {
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        }
    }

    @Override
    public void logout() {

    }

    @Override
    public User create(UserCreateRequest request) {

        if(userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);

        User user = userMapper.toUser(request);

        //hash password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        //set role
        user.setRole(Role.MEMBER);

        return userRepository.save(user);
    }


    @Override
    public UserResponse viewProfile() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

    @Override
    public void updateProfile(User user) {

    }

    @Override
    public HashMap<Integer, User> getAllBreeder() {
        return null;
    }

    public void updateAvatar(int id, String avatarUrl){
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);
    }

    @Override
    public User getUserById(int id){
        return userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }
}
