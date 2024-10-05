package swp.auctionkoi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.request.ApiResponse;
import swp.auctionkoi.dto.request.UserCreateRequest;
import swp.auctionkoi.models.User;
import swp.auctionkoi.service.user.UserService;

import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    ApiResponse<User> createAccount(@RequestBody @Valid UserCreateRequest request) {
        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.createAccount(request));
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


}
