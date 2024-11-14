package swp.auctionkoi.service.transaction;

import swp.auctionkoi.models.Transaction;

import java.util.List;

public interface MemberTransactionService {
    public List<Transaction> getAllTransactionsByUserId(int userId);
}
