package swp.auctionkoi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.request.delivery.UpdateDeliveryRequest;
import swp.auctionkoi.models.enums.DeliveryStatus;
import swp.auctionkoi.service.delivery.DeliveryService;

@RestController
@RequestMapping("/deliveries")
public class DeliveryController {

    @Autowired
    DeliveryService deliveryService;

    @PutMapping("/status")
    public ResponseEntity<?> updateDeliveryStatus(@RequestBody UpdateDeliveryRequest request) {

        deliveryService.updateDeliveryStatus(request.getDeliveryId(), request.getStatus());
        return ResponseEntity.ok("Delivery status updated successfully");
    }
}

