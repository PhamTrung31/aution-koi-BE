package swp.auctionkoi.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Nationalized;
import org.springframework.lang.Nullable;
import swp.auctionkoi.models.enums.DeliveryStatus;
import swp.auctionkoi.models.enums.TransactionType;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
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

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "auction_id")
    @Nullable
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    Auction auction;

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
    Float deliveryFee;

}