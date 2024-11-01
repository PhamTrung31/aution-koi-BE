package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.auctionkoi.models.KoiFish;

import java.util.List;


public interface KoiFishRepository extends JpaRepository<KoiFish, Integer> {
    List<KoiFish> findByBreederId(int breederId);
}
