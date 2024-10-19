package swp.auctionkoi.service.payment;

public interface PaymentService {
    public String requestWithdrawMoney(int userId, double amount) throws Exception;
}
