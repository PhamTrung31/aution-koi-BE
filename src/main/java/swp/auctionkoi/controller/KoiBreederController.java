package swp.auctionkoi.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swp.auctionkoi.dto.request.ApiResponse;
import swp.auctionkoi.dto.request.UserCreateRequest;
import swp.auctionkoi.models.User;
import swp.auctionkoi.service.user.KoiBreederService;

@RestController
@RequestMapping("/koibreeder")
public class KoiBreederController {

    @Autowired
    private KoiBreederService koiBreederService;

    @PostMapping("/create")
    ApiResponse<User> createAccount(@RequestBody @Valid UserCreateRequest request) {
        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setResult(koiBreederService.createAccountBreeder(request));
        return apiResponse;
    }

}
