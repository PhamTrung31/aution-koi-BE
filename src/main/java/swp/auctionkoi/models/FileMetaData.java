package swp.auctionkoi.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Entity
@Table(name = "files")
public class FileMetaData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "unique_id", nullable = false)
    private String uniqueId;

    @Column(name = "object_name", nullable = false)
    private String objectName;

    @Column(name = "upload_date", nullable = false)
    private LocalDateTime uploadDate;
}

