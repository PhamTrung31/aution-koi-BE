package swp.auctionkoi.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.request.user.StaffCreateUserRequest;
import swp.auctionkoi.dto.request.user.StaffUpdateUserRequest;
import swp.auctionkoi.dto.respone.ApiResponse;
import swp.auctionkoi.dto.request.user.UserCreateRequest;
import swp.auctionkoi.dto.request.user.UserUpdateRequest;
import swp.auctionkoi.dto.respone.user.UserResponse;
import swp.auctionkoi.models.User;
import swp.auctionkoi.service.user.StaffService;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/staffs")
public class StaffController {

    @Autowired
    private StaffService staffService;

    @GetMapping("/all")
    public ApiResponse<List<UserResponse>> getAllUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        authentication.getAuthorities().forEach(grantedAuthority -> log.info("GrantedAuthority {}", grantedAuthority));

        return ApiResponse.<List<UserResponse>>builder()
                .result(staffService.getAllMemberAndBreeder())
                .build();
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUser(@PathVariable int userId) {
        UserResponse userResponse = staffService.getUserById(userId);

        return ApiResponse.<UserResponse>builder()
                .code(200)
                .result(userResponse)
                .build();
    }

    @PostMapping("/create")
    public ApiResponse<UserResponse> addUser(@RequestBody @Valid StaffCreateUserRequest request) {
        UserResponse userResponse = staffService.addUser(request);

        return ApiResponse.<UserResponse>builder()
                .code(200)
                .result(userResponse)
                .message("Add user successfully!")
                .build();
    }

    @PostMapping("/ban/{userId}")
    public ApiResponse<String> banUser(@PathVariable int userId) {
        staffService.banUser(userId);

        return ApiResponse.<String>builder()
                .code(200)
                .message("Ban user successfully!")
                .build();
    }

    @PostMapping("/unban/{userId}")
    public ApiResponse<String> unbanUser(@PathVariable int userId) {
        staffService.unBanUser(userId);

        return ApiResponse.<String>builder()
                .code(200)
                .message("Unban user successfully!")
                .build();
    }

    @PutMapping("/{userId}")
    public ApiResponse<UserResponse> updateUser(@RequestBody @Valid StaffUpdateUserRequest tryUpdateUser) {
        UserResponse userResponse = staffService.updateUser(tryUpdateUser);
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .result(userResponse)
                .message("Update successfully!")
                .build();
    }
    @DeleteMapping("/{userId}")
    public ApiResponse<String> deleteUser(@PathVariable int userId) {
        boolean deleteSuccess = staffService.deleteUser(userId);
        if (deleteSuccess) {
            return ApiResponse.<String>builder()
                    .code(200)
                    .message("Deleted successfully!")
                    .build();
        } else {
            return ApiResponse.<String>builder()
                    .code(404)
                    .message("Delete failed!")
                    .build();
        }
    }
}

