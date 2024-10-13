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
    Auction auction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    swp.auctionkoi.models.User member;

    @Column(name = "payment_id")
    Integer paymentId;

    @Column(name = "wallet_id")
    Integer walletId;

    @Column(name = "transaction_fee")
    Double transactionFee;

    @Column(name = "transaction_float")
    Integer transactionFloat;


    @Column(name = "transaction_date")
    Instant transactionDate;

    @Column(name = "transaction_type")
    @Enumerated(EnumType.ORDINAL)
    TransactionType transactionType;

}