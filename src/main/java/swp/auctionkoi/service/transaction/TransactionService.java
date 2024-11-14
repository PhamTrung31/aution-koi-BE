package swp.auctionkoi.service.transaction;

import swp.auctionkoi.models.Transaction;

import java.util.List;

public interface TransactionService {
    public List<Transaction> getSortedTransactions(String sortBy, String order);
    public List<Transaction> getTransactionsByUserId(Integer userId);
}
