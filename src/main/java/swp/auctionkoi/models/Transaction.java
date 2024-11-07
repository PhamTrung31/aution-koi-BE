    package swp.auctionkoi.models;

    import com.fasterxml.jackson.annotation.*;
    import jakarta.persistence.*;
    import lombok.*;
    import lombok.experimental.FieldDefaults;
    import org.hibernate.annotations.ColumnDefault;
    import org.hibernate.annotations.DynamicInsert;
    import swp.auctionkoi.models.enums.Role;
    import swp.auctionkoi.models.enums.TransactionType;

    import java.time.Instant;

    @Getter
    @Setter
    @Entity
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    @DynamicInsert
    @Table(name = "Transactions")
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public class Transaction {
        @Id
        @Column(name = "id", nullable = false)
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Integer id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "auction_id")
        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
        @JsonIdentityReference(alwaysAsId = true)
        Auction auction;

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "user_id")
        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
        @JsonIdentityReference(alwaysAsId = true)
        swp.auctionkoi.models.User user;


        @Column(name = "payment_id")
        Integer paymentId;

        @Column(name = "wallet_id")
        Integer walletId;

        @Column(name = "amount")
        Float amount;

        @Column(name = "transaction_fee")
        Float transactionFee;

        @Column(name = "transaction_date")
        Instant transactionDate;

        @Column(name = "transaction_type")
        @Enumerated(EnumType.ORDINAL)
        TransactionType transactionType;


    //    @JsonProperty("user")
    //    public Integer getUserId() {
    //        return user != null ? user.getId() : null;
    //    }
    //
    //    @JsonProperty("auction")
    //    public Integer getAuctionId() {
    //        return auction != null ? auction.getId() : null;
    //    }
    }