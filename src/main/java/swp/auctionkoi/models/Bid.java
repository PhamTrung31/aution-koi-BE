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
@Builder
@Table(name = "Bids")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Bid {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id")
    Auction auction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    swp.auctionkoi.models.User user;

    @Column(name = "bid_created_date")
    Instant bidCreatedDate;

    @Column(name = "is_auto_bid")
    Boolean isAutoBid;

    @Column(name = "auto_bid_max")
    float autoBidMax;

    @Column(name = "increment_autobid")
    float incrementAutobid;

    @Column(name = "bid_amount")
    float bidAmount;

}