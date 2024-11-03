package swp.auctionkoi.repository;

import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import swp.auctionkoi.models.KoiFish;

import java.util.List;


public interface KoiFishRepository extends JpaRepository<KoiFish, Integer> {
    List<KoiFish> findByBreederId(int breederId);

    boolean existsByNameAndBreederId(@Size(max = 250) String name, int breederId);

    boolean existsByNameAndBreederIdAndIdNot(String name, Integer id, int id1);
}
