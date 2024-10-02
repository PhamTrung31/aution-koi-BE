package swp.auctionkoi.service.koifish.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp.auctionkoi.models.KoiFish;
import swp.auctionkoi.repository.KoiFishRepository;

@Service
@Transactional

public class KoiFishServiceImpl {

    @Autowired
    private KoiFishRepository koiFishRepository;

    public KoiFish addKoiFish(KoiFish koiFish) {
        return koiFishRepository.save(koiFish);
    }
}
