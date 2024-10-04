package swp.auctionkoi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.request.ApiResponse;
import swp.auctionkoi.dto.request.UserCreateRequest;
import swp.auctionkoi.models.User;
import swp.auctionkoi.service.user.UserService;

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


}
