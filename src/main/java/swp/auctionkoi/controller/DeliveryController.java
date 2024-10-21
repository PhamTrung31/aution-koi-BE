package swp.auctionkoi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.request.delivery.UpdateDeliveryRequest;
import swp.auctionkoi.dto.respone.ApiResponse;
import swp.auctionkoi.models.Delivery;
import swp.auctionkoi.models.enums.DeliveryStatus;
import swp.auctionkoi.service.delivery.DeliveryService;

@RestController
@RequestMapping("/deliveries")
public class DeliveryController {

    @Autowired
    DeliveryService deliveryService;

    @PutMapping("/status")
    public ApiResponse<?> updateDeliveryStatus(@RequestBody UpdateDeliveryRequest request) {

        deliveryService.updateDeliveryStatus(request.getDeliveryId(), request.getStatus());
        return ApiResponse.builder()
                .result(true)
                .code(200)
                .message("Update delivery successfully!")
                .build();
    }

    @GetMapping("/{deliveryId}")
    public ApiResponse<?> getDeliveryById(@PathVariable("deliveryId") int deliveryId) {
        Delivery delivery = deliveryService.getDeliveryById(deliveryId);
        return ApiResponse.builder()
                .result(delivery)
                .code(200)
                .message("Successfully!")
                .build();
    }


}

