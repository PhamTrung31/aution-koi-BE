package swp.auctionkoi.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.request.delivery.UpdateDeliveryRequest;
import swp.auctionkoi.dto.respone.ApiResponse;
import swp.auctionkoi.models.Delivery;
import swp.auctionkoi.models.enums.DeliveryStatus;
import swp.auctionkoi.service.delivery.DeliveryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/deliveries")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeliveryController {

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

    @GetMapping("/alldelivery")
    public ApiResponse<List<Delivery>> getAllDelivery() {
        List<Delivery> delivery = deliveryService.getAllDelivery();
        return ApiResponse.<List<Delivery>>builder()
                .result(delivery)
                .code(200)
                .message("Successfully!")
                .build();
    }


}

