package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.auctionkoi.models.KoiFishImage;

@Repository
public interface KoiFishImageRepository extends JpaRepository<KoiFishImage, Integer> {
}
