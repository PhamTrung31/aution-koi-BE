package swp.auctionkoi.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;
import swp.auctionkoi.models.enums.KoiStatus;
import swp.auctionkoi.models.enums.Sex;

import java.time.Instant;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Koi_Fish")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KoiFish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "breeder_id", nullable = false)
    User user;

    @Size(max = 250)
    @NotNull
    @Column(name = "name", nullable = false, length = 250)
    String name;

    @NotNull
    @Column(name = "sex", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    Sex sex;

    @NotNull
    @Column(name = "\"size\"", nullable = false)
    Float size;

    @NotNull
    @Column(name = "age", nullable = false)
    Integer age;

    @Size(max = 2000)
    @Nationalized
    @Column(name = "description", length = 2000)
    String description;

    @Size(max = 2000)
    @NotNull
    @Column(name = "image_url", nullable = false, length = 2000)
    String imageUrl;

    @Size(max = 2000)
    @NotNull
    @Column(name = "video_url", nullable = false, length = 2000)
    String videoUrl;

    @NotNull
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    KoiStatus status;

    @NotNull
    @ColumnDefault("getdate()")
    @Column(name = "fish_created_date", nullable = false)
    Instant fishCreatedDate;

    @NotNull
    @ColumnDefault("getdate()")
    @Column(name = "fish_updated_date", nullable = false)
    Instant fishUpdatedDate;

}