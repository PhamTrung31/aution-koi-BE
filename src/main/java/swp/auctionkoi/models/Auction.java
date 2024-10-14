package swp.auctionkoi.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;

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
    float currentPrice;

    @Column(name = "status")
    Integer status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private User winner;

    @Column(name = "highest_price")
    private float highestPrice;

    @ColumnDefault("60")
    @Column(name = "extension_seconds")
    private int extensionSeconds;

}