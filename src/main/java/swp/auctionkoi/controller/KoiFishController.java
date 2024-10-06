package swp.auctionkoi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swp.auctionkoi.models.KoiFish;
import swp.auctionkoi.service.koifish.KoiFishService;
//import swp.auctionkoi.service.koifish.impl.;

@RestController
@RequestMapping("/api/koifish")
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
}

