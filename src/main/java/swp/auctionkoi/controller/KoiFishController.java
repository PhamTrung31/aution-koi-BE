package swp.auctionkoi.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.request.koifish.KoiFishUpdateRequest;
import swp.auctionkoi.dto.respone.ApiResponse;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.KoiFish;
import swp.auctionkoi.models.User;
import swp.auctionkoi.models.enums.Role;
import swp.auctionkoi.repository.UserRepository;
import swp.auctionkoi.service.koifish.KoiFishService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/koifish")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin("*")
public class KoiFishController {

    KoiFishService koiFishService;

    private UserRepository userRepository;

    private User getUserFromContext(){
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        return userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    @GetMapping("/koifish/{id}")
    public ApiResponse<KoiFish> getKoiFishById(@PathVariable int id) {
        KoiFish koiFish = koiFishService.getKoiFishById(id);
        return ApiResponse.<KoiFish>builder()
                .code(200)
                .message("Successfully")
                .result(koiFish)
                .build();
    }

    @PostMapping("/fish/create")
    public ApiResponse<KoiFish> createKoiFish(@RequestBody KoiFish koiFish) {

        User breeder = getUserFromContext();

        if(!breeder.getRole().equals(Role.BREEDER)){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        KoiFish createdKoiFish = koiFishService.createKoiFish(koiFish, breeder.getId());
        return ApiResponse.<KoiFish>builder()
                .code(200)
                .message("Successfully")
                .result(createdKoiFish)
                .build();
    }

    // Update an existing KoiFish by ID
    @PutMapping("/{fishId}")
    public ApiResponse<KoiFish> updateKoiFish(@PathVariable Integer fishId, @RequestBody KoiFishUpdateRequest koiFish) {
        KoiFish updatedKoiFish = koiFishService.updateKoiFish(fishId, koiFish);
        return ApiResponse.<KoiFish>builder()
                .code(200)
                .message("Successfully")
                .result(updatedKoiFish)
                .build();
    }

    // View KoiFish by breeder ID in NEW
    @GetMapping("/fish/view/new")
    public ApiResponse<List<KoiFish>> viewKoiFishByBreederIdInNew() {

        User breeder = getUserFromContext();

        if(!breeder.getRole().equals(Role.BREEDER)){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        List<KoiFish> koiFishList = koiFishService.viewKoiFishByBreederIdInNew(breeder.getId());
        return ApiResponse.<List<KoiFish>>builder()
                .code(200)
                .message("Successfully")
                .result(koiFishList)
                .build();
    }
    // View KoiFish by breeder ID
    @GetMapping("/fish/view")
    public ApiResponse<List<KoiFish>> viewKoiFishByBreederId() {

        User breeder = getUserFromContext();

        if(!breeder.getRole().equals(Role.BREEDER)){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        List<KoiFish> koiFishList = koiFishService.viewKoiFishByBreederId(breeder.getId());
        return ApiResponse.<List<KoiFish>>builder()
                .code(200)
                .message("Successfully")
                .result(koiFishList)
                .build();
    }


    // Cancel a KoiFish by ID
    @PutMapping("/cancel/{id}")
    public ApiResponse<KoiFish> cancelKoiFish(@PathVariable int id) {
        KoiFish canceledKoiFish = koiFishService.cancelKoiFish(id);
        return ApiResponse.<KoiFish>builder()
                .code(200)
                .message("Successfully")
                .result(canceledKoiFish)
                .build();
    }
}
