package swp.auctionkoi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.models.Auction;
import swp.auctionkoi.service.auction.AuctionService;
import swp.auctionkoi.service.auction.impl.AuctionServiceImpl;

import java.util.Optional;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    @Autowired
    private AuctionServiceImpl auctionService;

    @PostMapping("/{auctionId}/join")

    public ResponseEntity<String> joinAuction(@PathVariable int auctionId,
                                              @RequestBody int memberId,
                                              @RequestParam Double depositAmount) {
        auctionService.joinAuctionWithDeposit(auctionId, memberId, depositAmount);
        return ResponseEntity.ok("Joined auction and deposit placed successfully");
    }

    @PostMapping("/{auctionId}/result")
    public ResponseEntity<String> handleAuctionResult(@PathVariable Long auctionId,
                                                      @RequestParam Long winningMemberId) {
        auctionService.handleAuctionResult(Math.toIntExact(auctionId), Math.toIntExact(winningMemberId));
        return ResponseEntity.ok("Auction result handled and deposits refunded");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Auction> getAuctionById(@PathVariable int id) {
        Optional<Auction> auction = auctionService.getAuction(id);
        return auction.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Add a new auction (staff only)
    @PostMapping

    public ResponseEntity<Auction> addAuction(@RequestBody Auction auction) {
        Auction newAuction = auctionService.addAuction(auction);
        return new ResponseEntity<>(newAuction, HttpStatus.CREATED);
    }

    // Update an existing auction (staff only)
    @PutMapping("/{id}")

    public ResponseEntity<Auction> updateAuction(@PathVariable int id, @RequestBody Auction auction) {
        auction.setId(id);  // Ensure the ID is set correctly
        Auction updatedAuction = auctionService.updateAuction(auction);
        return ResponseEntity.ok(updatedAuction);
    }

    // Delete an auction (staff only)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuction(@PathVariable int id) {
        auctionService.deleteAuction(id);
        return ResponseEntity.noContent().build();
    }
}

