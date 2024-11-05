package swp.auctionkoi.service.delivery;

import swp.auctionkoi.models.Delivery;
import swp.auctionkoi.models.enums.DeliveryStatus;

import java.util.List;

public interface DeliveryService {
    public void updateDeliveryStatus(int deliveryId, DeliveryStatus status);

    List<Delivery> getAllDelivery();
}
