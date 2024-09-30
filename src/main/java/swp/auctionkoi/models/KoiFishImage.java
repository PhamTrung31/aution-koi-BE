package swp.auctionkoi.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "KoiFishImage")
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KoiFishImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "koiFishId")
    KoiFish koiFish;

    @Size(max = 255)
    @Column(name = "imageUrl")
    String imageUrl;

    @ColumnDefault("getdate()")
    @Column(name = "uploadDate")
    Instant uploadDate;

}