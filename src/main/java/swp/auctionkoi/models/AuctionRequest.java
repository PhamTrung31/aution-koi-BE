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
@Table(name = "AuctionRequests")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuctionRequest {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "breeder_id")
    swp.auctionkoi.models.User breeder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fish_id")
    swp.auctionkoi.models.KoiFish fish;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id")
    swp.auctionkoi.models.Auction auction;

    @Column(name = "buy_out")
    Double buyOut;

    @Column(name = "start_price")
    Double startPrice;

    @Column(name = "increment_price")
    Double incrementPrice;

    @Column(name = "method_type")
    Integer methodType;

    @ColumnDefault("getdate()")
    @Column(name = "request_created_date")
    Instant requestCreatedDate;

    @ColumnDefault("getdate()")
    @Column(name = "request_updated_date")
    Instant requestUpdatedDate;

    @Column(name = "request_status")
    Integer requestStatus;

}