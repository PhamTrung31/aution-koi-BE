package swp.auctionkoi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Builder
@Table(name = "Wallets")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Wallet {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    User user;

    @Column(name = "balance")
    float balance;

//    @JsonProperty("userId")
//    public Integer getUserId() {
//        return user != null ? user.getId() : null;
//    }
}



//    public Integer getAuctionId() {
//        return auction != null ? auction.getId() : null;
//    }

