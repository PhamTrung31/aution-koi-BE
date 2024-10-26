package swp.auctionkoi.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.request.user.UserLoginRequest;
import swp.auctionkoi.dto.respone.ApiResponse;
import swp.auctionkoi.dto.request.user.UserCreateRequest;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.request.AvatarRequest;
import swp.auctionkoi.dto.request.user.UserUpdateRequest;
import swp.auctionkoi.dto.respone.ApiResponse;
import swp.auctionkoi.dto.request.user.UserCreateRequest;
import swp.auctionkoi.dto.respone.user.UserResponse;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.respone.ApiResponse;
import swp.auctionkoi.dto.request.user.UserCreateRequest;
import swp.auctionkoi.dto.respone.user.UserResponse;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.User;
import swp.auctionkoi.service.user.UserService;

import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @ExceptionHandler(AppException.class)
    ApiResponse<UserResponse> login(@RequestBody @Valid UserLoginRequest userLoginRequest) {
        UserResponse userResponse = userService.login(userLoginRequest);
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .message("Login successful")
                .result(userResponse)
                .build();
    }

    @PostMapping("/create")
    ApiResponse<User> createAccount(@RequestBody @Valid UserCreateRequest request) {
        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.create(request));
        return apiResponse;
    }

    @GetMapping("/allbreeder")
    public ApiResponse<HashMap<Integer, User>> getAllBreeder() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Username {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info("GrantedAuthority {}", grantedAuthority));

        return ApiResponse.<HashMap<Integer, User>>builder()
                .result(userService.getAllBreeder())
                .build();
    }


    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.viewProfile())
                .build();
    }

    @PutMapping("/{id}/avatar")
    public ApiResponse<String> updateAvatar(@PathVariable int id, @RequestBody @Valid AvatarRequest avatarRequest) {

        userService.updateAvatar(id, avatarRequest.getAvatarUrl());
        return ApiResponse.<String>builder()
                .result(String.valueOf(true))
                .code(200)
                .message("Avatar updated successfully")
                .build();

    }
}
