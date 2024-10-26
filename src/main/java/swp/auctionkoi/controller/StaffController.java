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
import swp.auctionkoi.service.transaction.TransactionService;
import swp.auctionkoi.service.user.StaffService;

import java.util.HashMap;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/staffs")

public class StaffController {

    @Autowired
    private StaffService staffService;


    @GetMapping("/all")
    public ApiResponse<HashMap<Integer, UserResponse>> getAllUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Username {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info("GrantedAuthority {}", grantedAuthority));

        return ApiResponse.<HashMap<Integer, UserResponse>>builder()
                .result(staffService.getAllUser())
                .build();
    }



    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable int userId) {
        Optional<UserResponse> userOptional = staffService.getUser(userId);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    @PostMapping
    public ResponseEntity<UserResponse> addUser(@RequestBody UserCreateRequest request) {
        Optional<UserResponse> userOptional = staffService.addUser(request);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable int userId, UserUpdateRequest tryUpdateUser) {
        Optional<UserResponse> userOptional = staffService.updateUser(userId, tryUpdateUser);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable int userId) {
        boolean deleteSuccess = staffService.deleteUser(userId);
        if (deleteSuccess) {
            return ResponseEntity.ok("User deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

}
