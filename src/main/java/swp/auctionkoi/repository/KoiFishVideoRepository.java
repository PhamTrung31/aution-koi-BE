package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.auctionkoi.models.KoiFishVideo;

@Repository
public interface KoiFishVideoRepository extends JpaRepository<KoiFishVideo, Integer> {
}