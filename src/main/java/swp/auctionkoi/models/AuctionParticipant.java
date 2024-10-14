package swp.auctionkoi.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "AuctionParticipants")
public class AuctionParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Column(name = "deposit_amount", nullable = false)
    private float depositAmount;

    @Column(name = "bid_price")
    private Double bidPrice;

    @ColumnDefault("getdate()")
    @Column(name = "join_date")
    private Instant joinDate;
}