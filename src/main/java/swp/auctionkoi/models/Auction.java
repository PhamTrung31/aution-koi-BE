package swp.auctionkoi.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Auctions")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Auction {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fish_id")
    swp.auctionkoi.models.KoiFish fish;

    @Column(name = "current_price")
    Float currentPrice;

    @Column(name = "status")
    Integer status;

    @Column(name = "deposit_amount")
    Float depositAmount;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private User winner;

}