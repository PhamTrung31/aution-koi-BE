package swp.auctionkoi.models;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
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
@Table(name = "auction_requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuctionRequest {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @NotNull
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)

    swp.auctionkoi.models.User user; //breeder

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_staff_id")
    @Nullable
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    swp.auctionkoi.models.User assignedStaff;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fish_id")
    @NotNull
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    swp.auctionkoi.models.KoiFishs fish;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id")
    @Nullable
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    swp.auctionkoi.models.Auction auction;

    @Column(name = "buy_out")
    @NotNull
    Float buyOut;

    @Column(name = "increment_step")
    @NotNull
    Integer incrementStep;

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