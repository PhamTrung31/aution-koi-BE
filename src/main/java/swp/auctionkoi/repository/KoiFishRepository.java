package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.auctionkoi.models.KoiFish;

public interface KoiFishRepository extends JpaRepository<KoiFish, Integer> {
}
