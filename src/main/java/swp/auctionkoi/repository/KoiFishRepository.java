package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.auctionkoi.models.KoiFishs;

import java.util.List;


public interface KoiFishRepository extends JpaRepository<KoiFishs, Integer> {
    List<KoiFishs> findByBreederId(int breederId);
}
