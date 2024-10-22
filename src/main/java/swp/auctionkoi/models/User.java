package swp.auctionkoi.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Nationalized;
import org.springframework.lang.Nullable;
import swp.auctionkoi.models.enums.Role;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "Users")
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Size(max = 70)
    @Column(name = "username", length = 70)
    @NotNull
    String username;

    @Size(max = 100)
    @Column(name = "password", length = 100)
    @NotNull
    String password;

    @Column(name = "role")
    @Enumerated(EnumType.ORDINAL)
    @NotNull
    Role role;

    @Size(max = 250)
    @Nationalized
    @Column(name = "fullname", length = 250)
    String fullname;

    @Size(max = 20)
    @Column(name = "phone", length = 20)
    String phone;

    @Size(max = 250)
    @Nationalized @Column(name = "address", length = 250)
    String address;

    @Column(name = "is_active")
    @NotNull
    Boolean isActive;

    @Column(name = "avatar_url")
    String avatarUrl;

    @Column(name = "user_created_date")
    Instant userCreatedDate;

    @Column(name = "user_updated_date")
    Instant userUpdatedDate;
}