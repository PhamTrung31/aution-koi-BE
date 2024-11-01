package swp.auctionkoi.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.request.koifish.KoiFishUpdateRequest;
import swp.auctionkoi.dto.respone.ApiResponse;
import swp.auctionkoi.models.KoiFishs;
import swp.auctionkoi.service.koifish.impl.KoiFishServiceImpl;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/koifish")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin("*")
public class KoiFishController {

    KoiFishServiceImpl koiFishService;

    @GetMapping("/koifish/{id}")
    public ApiResponse<KoiFishs> getKoiFishById(@PathVariable int id) {
        KoiFishs koiFish = koiFishService.getKoiFishById(id);
        return ApiResponse.<KoiFishs>builder()
                .code(200)
                .message("Successfully")
                .result(koiFish)
                .build();
    }

    @PostMapping("/{breederId}")
    public ApiResponse<KoiFishs> createKoiFish(@RequestBody KoiFishs koiFish ,@PathVariable int breederId) {
        KoiFishs createdKoiFish = koiFishService.createKoiFish(koiFish, breederId);
        return ApiResponse.<KoiFishs>builder()
                .code(200)
                .message("Successfully")
                .result(createdKoiFish)
                .build();
    }

    // Update an existing KoiFish by ID
    @PutMapping("/{id}")
    public ApiResponse<KoiFishs> updateKoiFish(@PathVariable Integer id, @RequestBody KoiFishUpdateRequest koiFish) {
        KoiFishs updatedKoiFish = koiFishService.updateKoiFish(id, koiFish);
        return ApiResponse.<KoiFishs>builder()
                .code(200)
                .message("Successfully")
                .result(updatedKoiFish)
                .build();
    }

    // View KoiFish by breeder ID
    @GetMapping("/breeder/{breederId}")
    public ApiResponse<List<KoiFishs>> viewKoiFishByBreederId(@PathVariable int breederId) {
        List<KoiFishs> koiFishList = koiFishService.viewKoiFishByBreederId(breederId);
        return ApiResponse.<List<KoiFishs>>builder()
                .code(200)
                .message("Successfully")
                .result(koiFishList)
                .build();
    }

    // Cancel a KoiFish by ID
    @PutMapping("/cancel/{id}")
    public ApiResponse<KoiFishs> cancelKoiFish(@PathVariable int id) {
        KoiFishs canceledKoiFish = koiFishService.cancelKoiFish(id);
        return ApiResponse.<KoiFishs>builder()
                .code(200)
                .message("Successfully")
                .result(canceledKoiFish)
                .build();
    }
}
