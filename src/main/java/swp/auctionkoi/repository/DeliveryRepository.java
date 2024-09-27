package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.auctionkoi.models.Delivery;

public interface DeliveryRepository extends JpaRepository<Delivery, Integer> {
}
