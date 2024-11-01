    package swp.auctionkoi.models;

    import com.fasterxml.jackson.annotation.JsonIdentityInfo;
    import com.fasterxml.jackson.annotation.JsonIdentityReference;
    import com.fasterxml.jackson.annotation.ObjectIdGenerators;
    import com.google.firebase.database.annotations.NotNull;
    import jakarta.persistence.*;
    import jakarta.validation.constraints.Size;
    import lombok.*;
    import lombok.experimental.FieldDefaults;
    import org.hibernate.annotations.ColumnDefault;
    import org.hibernate.annotations.DynamicInsert;
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
    @DynamicInsert
    @Table(name = "Koi_Fish")
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public class KoiFishs {
        @Id
        @Column(name = "id", nullable = false)
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Integer id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "breeder_id")
        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
        @JsonIdentityReference(alwaysAsId = true)
        swp.auctionkoi.models.User breeder;

        @Size(max = 250)
        @Column(name = "name", length = 250)
        String name;

        @Column(name = "sex")
        @Enumerated(EnumType.ORDINAL)
        Sex sex;

        @Column(name = "size")
        Double size;

        @Column(name = "age")
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
        @Column(name = "status")
        @Enumerated(EnumType.ORDINAL)
        KoiStatus status;

        @ColumnDefault("getdate()")
        @Column(name = "fish_created_date")
        Instant fishCreatedDate;

        @ColumnDefault("getdate()")
        @Column(name = "fish_updated_date")
        Instant fishUpdatedDate;

    }