package swp.auctionkoi.dto.request.payment;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WithdrawPaymentRequest {
     Integer userId;
     Double amount;
}
