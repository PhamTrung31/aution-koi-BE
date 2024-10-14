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
@Builder
@Table(name = "Transactions")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Transaction {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id")
    Auction auction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    swp.auctionkoi.models.User member;

    @Column(name = "payment_id")
    Integer paymentId;

    @Column(name = "wallet_id")
    Integer walletId;

    @Column(name = "transaction_fee")
    float transactionFee;

    @Column(name = "bid_price")
    float bidPrice;

    @Column(name = "transaction_type")
    Integer transactionType;

    @ColumnDefault("getdate()")
    @Column(name = "transaction_date")
    Instant transactionDate;

}