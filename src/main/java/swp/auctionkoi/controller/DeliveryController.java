package swp.auctionkoi.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.request.delivery.UpdateDeliveryRequest;
import swp.auctionkoi.models.enums.DeliveryStatus;
import swp.auctionkoi.service.delivery.DeliveryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/deliveries")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeliveryController {

    DeliveryService deliveryService;

    @PutMapping("/status")
    public ResponseEntity<?> updateDeliveryStatus(@RequestBody UpdateDeliveryRequest request) {

        deliveryService.updateDeliveryStatus(request.getDeliveryId(), request.getStatus());
        return ResponseEntity.ok("Delivery status updated successfully");
    }
}

