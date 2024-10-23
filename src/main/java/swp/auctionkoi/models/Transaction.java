package swp.auctionkoi.models;

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
@DynamicInsert
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
    @JoinColumn(name = "user_id")
    swp.auctionkoi.models.User user;

    @Column(name = "payment_id")
    Integer paymentId;

    @Column(name = "wallet_id")
    Integer walletId;

    @Column(name = "transaction_fee")
    float transactionFee;

    @Column(name = "amount")
    float amount;

    @Column(name = "transaction_type")
    @Enumerated(EnumType.ORDINAL)
    TransactionType transactionType;

    @Column(name = "transaction_date")
    Instant transactionDate;

}