package swp.auctionkoi.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.request.ApiResponse;
import swp.auctionkoi.dto.request.UserCreateRequest;
import swp.auctionkoi.dto.request.UserUpdateRequest;
import swp.auctionkoi.dto.respone.UserResponse;
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
                .build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getStaff(Integer id) {
        Optional<UserResponse> userResponse = managerService.getStaff(id);
        if (userResponse.isPresent()) {
            return ResponseEntity.ok(userResponse.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<User> addStaff(@RequestBody UserCreateRequest request) {

        Optional<User> user = managerService.addStaff(request);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<UserResponse> updateStaffStatus(@PathVariable int id,
                                                          @RequestBody UserUpdateRequest request) {
        Optional<UserResponse> user = managerService.updateActiveStaff(id, request);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteStaff(@PathVariable int id) {
        boolean del = managerService.deleteStaff(id);
        if (del) {
            return ResponseEntity.ok("Deleted Staff Successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Staff not found");
        }
    }
}
