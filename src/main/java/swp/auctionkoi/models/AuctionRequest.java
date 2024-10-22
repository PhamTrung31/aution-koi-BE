package swp.auctionkoi.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.lang.Nullable;
import swp.auctionkoi.models.enums.AuctionRequestStatus;
import swp.auctionkoi.models.enums.AuctionType;

import java.time.Instant;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "Auction_Requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuctionRequest {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @NotNull
    swp.auctionkoi.models.User user; //breeder

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fish_id")
    @NotNull
    swp.auctionkoi.models.KoiFish fish;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id")
    @Nullable
    swp.auctionkoi.models.Auction auction;

    @Column(name = "buy_out")
    @NotNull
    Float buyOut;

    @Column(name = "start_price")
    @NotNull
    Float startPrice;

    @Column(name = "method_type")
    @Enumerated(EnumType.ORDINAL)
    @NotNull
    AuctionType methodType;

    @Column(name = "request_created_date")
    Instant requestCreatedDate;

    @Column(name = "request_updated_date")
    Instant requestUpdatedDate;

    @Column(name = "request_status")
    @Enumerated(EnumType.ORDINAL)
    @NotNull
    AuctionRequestStatus requestStatus;

    @Column(name = "start_time")
    @NotNull
    Instant startTime;

    @Column(name = "end_time")
    @NotNull
    Instant endTime;

    @PrePersist
    public void onPrePersist() {
        this.requestCreatedDate = Instant.now();
        this.requestUpdatedDate = Instant.now();
    }
//
//    @PreUpdate
//    public void onPreUpdate() {
//        this.requestUpdatedDate = Instant.now();
//    }
}