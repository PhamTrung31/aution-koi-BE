package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.auctionkoi.models.KoiFishs;

public interface KoiFishRepository extends JpaRepository<KoiFishs, Integer> {
}
