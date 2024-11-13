package swp.auctionkoi.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.request.koifish.KoiFishUpdateRequest;
import swp.auctionkoi.dto.respone.ApiResponse;
import swp.auctionkoi.models.KoiFish;
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

    @GetMapping("/koifish/{id}")
    public ApiResponse<KoiFish> getKoiFishById(@PathVariable int id) {
        KoiFish koiFish = koiFishService.getKoiFishById(id);
        return ApiResponse.<KoiFish>builder()
                .code(200)
                .message("Successfully")
                .result(koiFish)
                .build();
    }

    @PostMapping("/{breederId}")
    public ApiResponse<KoiFish> createKoiFish(@RequestBody KoiFish koiFish ,@PathVariable int breederId) {
        KoiFish createdKoiFish = koiFishService.createKoiFish(koiFish, breederId);
        return ApiResponse.<KoiFish>builder()
                .code(200)
                .message("Successfully")
                .result(createdKoiFish)
                .build();
    }

    // Update an existing KoiFish by ID
    @PutMapping("/{id}")
    public ApiResponse<KoiFish> updateKoiFish(@PathVariable Integer id, @RequestBody KoiFishUpdateRequest koiFish) {
        KoiFish updatedKoiFish = koiFishService.updateKoiFish(id, koiFish);
        return ApiResponse.<KoiFish>builder()
                .code(200)
                .message("Successfully")
                .result(updatedKoiFish)
                .build();
    }

    // View KoiFish by breeder ID in NEW
    @GetMapping("/breeder/new/{breederId}")
    public ApiResponse<List<KoiFish>> viewKoiFishByBreederIdInNew(@PathVariable int breederId) {
        List<KoiFish> koiFishList = koiFishService.viewKoiFishByBreederIdInNew(breederId);
        return ApiResponse.<List<KoiFish>>builder()
                .code(200)
                .message("Successfully")
                .result(koiFishList)
                .build();
    }
    // View KoiFish by breeder ID
    @GetMapping("/breeder/{breederId}")
    public ApiResponse<List<KoiFish>> viewKoiFishByBreederId(@PathVariable int breederId) {
        List<KoiFish> koiFishList = koiFishService.viewKoiFishByBreederId(breederId);
        return ApiResponse.<List<KoiFish>>builder()
                .code(200)
                .message("Successfully")
                .result(koiFishList)
                .build();
    }


    // Delete a KoiFish by ID
    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteKoiFish(@PathVariable int id) {
        boolean del = koiFishService.cancelKoiFish(id);
        return ApiResponse.<String>builder()
                .code(200)
                .message("Successfully")
                .build();
    }
}
