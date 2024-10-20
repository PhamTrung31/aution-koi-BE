package swp.auctionkoi.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Payments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private swp.auctionkoi.models.User member;

    @Column(name = "amount")
    private float amount;

    @Column(name = "payment_status")
    private Integer paymentStatus;

}