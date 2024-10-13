package swp.auctionkoi.dto.request.delivery;

import lombok.*;
import lombok.experimental.FieldDefaults;
import swp.auctionkoi.models.enums.DeliveryStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateDeliveryRequest {
    int deliveryId;
    DeliveryStatus status;
}
