package swp.auctionkoi.service.payment.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.Payment;
import swp.auctionkoi.repository.PaymentRepository;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('STAFF')")
public class PaymentServiceImpl {
    private final PaymentRepository paymentRepository;

    public HashMap<Integer, Payment> getAllPayment(){
        HashMap<Integer, Payment> payments = new HashMap<>();
        List<Payment> paymentList = paymentRepository.findAll();
        if(paymentList.isEmpty()){
            throw new AppException(ErrorCode.NO_PAYMENT);
        }
        for(Payment payment : paymentList){
            payments.put(payment.getId(), payment);
        }
        return payments;
    }

    public Payment getPaymentById(int id){

    }
}
