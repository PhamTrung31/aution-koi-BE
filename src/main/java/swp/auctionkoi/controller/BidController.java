//package swp.auctionkoi.controller;
//
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import swp.auctionkoi.models.Bid;
//import swp.auctionkoi.repository.BidRepository;
//import swp.auctionkoi.service.bid.BidService;
//
//import java.util.List;
//
//@Slf4j
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/bids")
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@CrossOrigin("*")
//public class BidController {
//
//    BidService bidService;
//
//    @GetMapping("/top5")
//    public List<Bid> getTop5TraditionalBids() {
//        return bidService.getCachedTop5Bids();
//    }
//}
