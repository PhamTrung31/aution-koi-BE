package swp.auctionkoi.dto.request.transaction;

import lombok.*;
import lombok.experimental.FieldDefaults;
import swp.auctionkoi.models.enums.TransactionType;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionRequestFilterDTO {
    LocalDate fromDate;
    LocalDate toDate;
    TransactionType transactionType;
}
