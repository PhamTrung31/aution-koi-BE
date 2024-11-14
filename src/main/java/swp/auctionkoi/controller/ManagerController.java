package swp.auctionkoi.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.respone.ApiResponse;
import swp.auctionkoi.dto.request.user.UserCreateRequest;
import swp.auctionkoi.dto.request.user.UserUpdateRequest;
import swp.auctionkoi.dto.respone.user.UserResponse;
import swp.auctionkoi.models.User;
import swp.auctionkoi.service.user.ManagerService;
import swp.auctionkoi.service.user.impl.ManagerServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    private ManagerServiceImpl managerService;

    @GetMapping("/allstaff")
    public ApiResponse<List<User>> getAllStaff() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Username {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info("GrantedAuthority {}", grantedAuthority));

//        // Get all staff and filter out those who are not active
//        List<User> activeStaff = managerService.getAllStaff().stream()
//                .filter(User::getIsActive)  // Chỉ bao gồm staff active
//                .collect(Collectors.toList());

        return ApiResponse.<List<User>>builder()
                .result(managerService.getAllStaff())
                .code(200)
                .message("Successfully")
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getStaff(@PathVariable Integer id) {
        UserResponse userResponse = managerService.getStaff(id);

        return ApiResponse.<UserResponse>builder()
                .result(userResponse)
                .code(200)
                .message("Successfully")
                .build();
    }


    @PostMapping("/add")
    public ApiResponse<User> addStaff(@RequestBody @Valid UserCreateRequest request) {

        User user = managerService.addStaff(request);

        return ApiResponse.<User>builder()
                .result(user)
                .code(200)
                .message("Successfully")
                .build();

    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateStaff( @PathVariable  Integer id,
            @RequestBody @Valid UserUpdateRequest request) {
        UserResponse user = managerService.updateStaff(id, request);
        return ApiResponse.<UserResponse>builder()
                .result(user)
                .code(200)
                .message("Successfully")
                .build();


    }

    @DeleteMapping("/{id}/delete")
    public ApiResponse<String> deleteStaff(@PathVariable int id) {
        boolean del = managerService.deleteStaff(id);
        if (del) {
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

    @PostMapping("/ban/{userId}")
    public ApiResponse<String> banUser(@PathVariable int userId) {
        managerService.banUser(userId);

        return ApiResponse.<String>builder()
                .code(200)
                .message("Ban staff successfully!")
                .build();
    }

    @PostMapping("/unban/{userId}")
    public ApiResponse<String> unbanUser(@PathVariable int userId) {
        managerService.unBanUser(userId);

        return ApiResponse.<String>builder()
                .code(200)
                .message("Unban staff successfully!")
                .build();
    }
}
