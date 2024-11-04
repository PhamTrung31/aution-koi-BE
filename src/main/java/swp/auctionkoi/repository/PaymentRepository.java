package swp.auctionkoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.auctionkoi.models.Payment;
import swp.auctionkoi.models.enums.PaymentStatus;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);
}
