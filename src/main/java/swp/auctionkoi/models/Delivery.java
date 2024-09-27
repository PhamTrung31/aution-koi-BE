package swp.auctionkoi.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;

@Getter
@Setter
@Entity
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

    @Column(name = "status")
    Integer status;

    @Column(name = "delivery_date")
    LocalDate deliveryDate;

    @Column(name = "delivery_fee")
    Double deliveryFee;

}