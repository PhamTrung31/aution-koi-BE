package swp.auctionkoi.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;
import swp.auctionkoi.models.enums.AuctionStatus;

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

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fish_id")
    swp.auctionkoi.models.KoiFish fish;

    @Column(name = "current_price")
    Float currentPrice;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    AuctionStatus status;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "winner_id")
    User winner;

    @Column(name = "highest_prices", nullable = true)
    Float  highestPrice;

    @ColumnDefault("60")
    @Column(name = "extension_seconds")
    int extensionSeconds;

    @Column(name = "deposit_amount")
    Float depositAmount;

    @ColumnDefault("0")
    @Column(name = "buy_intent_count")
    private Integer buyIntentCount;
}