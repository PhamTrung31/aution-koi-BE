package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.auctionkoi.models.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}
