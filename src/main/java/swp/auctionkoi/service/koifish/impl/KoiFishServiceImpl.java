package swp.auctionkoi.service.koifish.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.request.koifish.KoiFishUpdateRequest;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.KoiFish;
import swp.auctionkoi.models.User;
import swp.auctionkoi.models.enums.KoiStatus;
import swp.auctionkoi.models.enums.Role;
import swp.auctionkoi.repository.KoiFishRepository;
import swp.auctionkoi.repository.UserRepository;
import swp.auctionkoi.service.koifish.KoiFishService;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KoiFishServiceImpl implements KoiFishService {

    KoiFishRepository koiFishRepository;
    UserRepository userRepository;

    @Override
    public KoiFish getKoiFishById(int id) {
        return koiFishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("KoiFish not found"));
    }

    @Override
    public KoiFish createKoiFish(KoiFish koiFish, int breederId) {
        User breeder = userRepository.findById(breederId)
                .orElseThrow(() -> new RuntimeException("Breeder not found"));
        // Check if the user's role is "BREEDER"
        if (!breeder.getRole().equals(Role.BREEDER)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        koiFish.setBreeder(breeder);  // Set breeder to avoid NULL in breeder_id column
        koiFish.setStatus(KoiStatus.NEW);
        return koiFishRepository.save(koiFish);
    }

    @Override
    public KoiFish updateKoiFish(int id, KoiFishUpdateRequest updatedKoiFish) {
        KoiFish existingKoiFish = koiFishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("KoiFish not found"));
        existingKoiFish.setName(updatedKoiFish.getName());
        existingKoiFish.setSex(updatedKoiFish.getSex());
        existingKoiFish.setSize(updatedKoiFish.getSize());
        existingKoiFish.setAge(updatedKoiFish.getAge());
        existingKoiFish.setDescription(updatedKoiFish.getDescription());
        existingKoiFish.setImageUrl(updatedKoiFish.getImageUrl());
        existingKoiFish.setVideoUrl(updatedKoiFish.getVideoUrl());
        return koiFishRepository.save(existingKoiFish);
    }

    @Override
    public List<KoiFish> viewKoiFishByBreederId(int breederId) {
        return koiFishRepository.findByBreederId(breederId);
    }


    @Override
    public KoiFish cancelKoiFish(int id) {
        KoiFish koiFish = koiFishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("KoiFish not found"));
        koiFish.setStatus(KoiStatus.CANCELED);
        return koiFishRepository.save(koiFish);
    }


}
