//package swp.auctionkoi.service.koifish.impl;
//
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import org.springframework.stereotype.Service;
//import swp.auctionkoi.dto.request.koifish.KoiFishCreateRequest;
//import swp.auctionkoi.dto.request.koifish.KoiFishUpdateRequest;
//import swp.auctionkoi.dto.respone.koifish.KoiFishResponse;
//import swp.auctionkoi.mapper.KoiFishMapper;
//import swp.auctionkoi.models.KoiFish;
//import swp.auctionkoi.repository.KoiFishRepository;
//import swp.auctionkoi.service.koifish.KoiFishService;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//public class KoiFishServiceImpl implements KoiFishService {
//
//    KoiFishRepository koiFishRepository;
//
//    KoiFishMapper koiFishMapper;
//
//
//
//    @Override
//    public HashMap<Integer, KoiFishResponse> getAllFish() {
//        HashMap<Integer, KoiFishResponse> result = new HashMap<>();
//
//        List<KoiFish> koiFishList = koiFishRepository.findAll();
//        for (KoiFish koiFish : koiFishList) {
//            result.put(koiFish.getId(), koiFishMapper.toKoiFishResponse(koiFish));
//        }
//        return result;
//    }
//
//    @Override
//    public Optional<KoiFishResponse> getFish(int id) {
//        KoiFish koiFish = koiFishRepository.findById(id).get();
//        return Optional.of(koiFishMapper.toKoiFishResponse(koiFish));
//    }
//
////    @Override
////    public KoiFishResponse addFish(KoiFishCreateRequest fish) {
////
////    }
//
//    @Override
//    public Optional<KoiFishResponse> updateFish(KoiFishUpdateRequest fish) {
//        return Optional.empty();
//    }
//
//    @Override
//    public boolean deleteFish(int id) {
//        return false;
//    }
//}
