package swp.auctionkoi.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Nationalized;
import swp.auctionkoi.models.enums.DeliveryStatus;
import swp.auctionkoi.models.enums.TransactionType;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Builder
@Table(name = "Deliveries")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Delivery {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    swp.auctionkoi.models.Transaction transaction;

    @Size(max = 250)
    @Nationalized
    @Column(name = "from_address", length = 250)
    String fromAddress;

    @Size(max = 250)
    @Nationalized
    @Column(name = "to_address", length = 250)
    String toAddress;

    @Column(name = "delivery_status")
    @Enumerated(EnumType.ORDINAL)
    DeliveryStatus deliveryStatus ;

    @Column(name = "delivery_date")
    Instant deliveryDate;

    @Column(name = "delivery_fee")
    Double deliveryFee;

}