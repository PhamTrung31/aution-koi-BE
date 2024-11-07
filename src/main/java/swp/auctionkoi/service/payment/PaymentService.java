package swp.auctionkoi.service.payment;

import swp.auctionkoi.models.Payment;

import java.util.List;

public interface PaymentService {
    List<Payment> getAllPayMentOfBreeder(int breederId);

    public String requestWithdrawMoney(int userId, float amount) throws Exception;

    List<Payment> getAllPendingWithdrawRequests();

    void approveWithdrawRequest(int paymentId) throws Exception;

    void rejectWithdrawRequest(int paymentId) throws Exception;
}
