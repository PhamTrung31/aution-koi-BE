package swp.auctionkoi.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequestDTO {
     @NotNull(message = "Amount cannot be null!")
     @DecimalMin(value = "0.0", inclusive = true, message = "Amount must be a positive number")
     long amount;
     String memberId;
}
