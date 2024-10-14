package swp.auctionkoi.service.delivery;

import swp.auctionkoi.models.enums.DeliveryStatus;

public interface DeliveryService {
    public void updateDeliveryStatus(int deliveryId, DeliveryStatus status);

}
