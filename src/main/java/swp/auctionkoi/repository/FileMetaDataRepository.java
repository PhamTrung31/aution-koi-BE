package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.auctionkoi.models.FileMetaData;

@Repository
public interface FileMetaDataRepository extends JpaRepository<FileMetaData, Integer> {
    FileMetaData findByUniqueId(String uniqueId);
}
