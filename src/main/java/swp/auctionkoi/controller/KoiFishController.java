package swp.auctionkoi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import swp.auctionkoi.models.KoiFishs;
import swp.auctionkoi.repository.KoiFishRepository;
//import swp.auctionkoi.service.koifish.impl.;

@RestController
@RequestMapping("/api/koifish")
@RequiredArgsConstructor
public class KoiFishController {

//    @Autowired
//    private KoiFishServiceImpl koiFishService;
//
//    // Add new KoiFish (Breeder Only)
//    @PostMapping
//    public ResponseEntity<KoiFish> addKoiFish(@RequestBody KoiFish koiFish) {
//        KoiFish newKoiFish = koiFishService.addKoiFish(koiFish);
//        return new ResponseEntity<>(newKoiFish, HttpStatus.CREATED);
//    }


    private final FileStorageService fileStorageService;

    private final KoiFishRepository koiFishRepository;

//    public KoiFishController(FileStorageService fileStorageService, KoiFishRepository koiFishRepository) {
//        this.fileStorageService = fileStorageService;
//        this.koiFishRepository = koiFishRepository;
//    }

    @PostMapping("/upload/{koiId}")
    public ResponseEntity<String> uploadKoiImage(@PathVariable Integer koiId,
                                                 @RequestParam("file") MultipartFile file) {
        try {
            // Kiểm tra xem KoiFish có tồn tại không
            KoiFishs koiFish = koiFishRepository.findById(koiId)
                    .orElseThrow(() -> new IllegalArgumentException("KoiFish not found"));

            // Kiểm tra xem file có rỗng không
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            // Tải lên file mới thông qua FileStorageService
            String imageUrl = fileStorageService.uploadFile(file);

//            // Xóa ảnh cũ nếu cần (nếu koiFish đã có ảnh trước đó)
//            if (koiFish.getImage() != null && !koiFish.getImage().isEmpty()) {
//                fileStorageService.deleteFile(koiFish.getImage());
//            }

            // Cập nhật URL ảnh mới cho KoiFish
            koiFish.setImage(imageUrl);
            koiFishRepository.save(koiFish);


            return ResponseEntity.ok("Image uploaded successfully for KoiFish with ID: " + koiId);
        } catch (IllegalArgumentException e) {
            // Xử lý ngoại lệ nếu KoiFish không được tìm thấy
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Xử lý các lỗi khác
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
        }
    }
}



