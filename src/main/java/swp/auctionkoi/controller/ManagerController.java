package swp.auctionkoi.controller;

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
    public ApiResponse<HashMap<Integer, User>> getAllStaff() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Username {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info("GrantedAuthority {}", grantedAuthority));

        // Get all staff and filter out those who are not active
        HashMap<Integer, User> activeStaff = managerService.getAllStaff().entrySet().stream()
                .filter(entry -> entry.getValue().getIsActive())  // Only include active staff (isActive == true)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, HashMap::new));

        return ApiResponse.<HashMap<Integer, User>>builder()
                .result(activeStaff)
                .code(200)
                .message("Successfully")
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getStaff(@PathVariable Integer id) {
        UserResponse userResponse = managerService.getStaff(id);

        if (userResponse != null) {
            return ApiResponse.<UserResponse>builder()
                    .result(userResponse)
                    .code(200)
                    .message("Successfully")
                    .build();
        }

        return ApiResponse.<UserResponse>builder()
                .result(null)
                .code(404)
                .message("Staff not found")
                .build();
    }


    @PostMapping("/add")
    public ApiResponse<User> addStaff(@RequestBody UserCreateRequest request) {

        User user = managerService.addStaff(request);
        if (user != null) {
            return ApiResponse.<User>builder()
                    .result(user)
                    .code(200)
                    .message("Successfully")
                    .build();
        }

        return ApiResponse.<User>builder()
                .result(null)
                .code(404)
                .message("Staff not found")
                .build();
    }

    @PutMapping("/{id}/status")
    public ApiResponse<UserUpdateRequest> updateStaff(@PathVariable int id,
                                                          @RequestBody UserUpdateRequest request) {
        UserResponse user = managerService.updateStaff(id, request);
        if (user != null) {
            return ApiResponse.<UserUpdateRequest>builder()
                    .result(request)
                    .code(200)
                    .message("Successfully")
                    .build();
        }

        return ApiResponse.<UserUpdateRequest>builder()
                .result(null)
                .code(404)
                .message("Staff not found")
                .build();
    }

    @DeleteMapping("/{id}/delete")
    public ApiResponse<Object> deleteStaff(@PathVariable int id) {
        boolean del = managerService.deleteStaff(id);
        if (del) {
            return ApiResponse.<Object>builder()
                    .code(200)
                    .message("Successfully")
                    .build();
        } else {
            return ApiResponse.<Object>builder()
                    .code(404)
                    .message("Staff not found")
                    .build();
        }
    }
}
