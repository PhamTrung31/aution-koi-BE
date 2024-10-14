package swp.auctionkoi.service.transaction.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.Transaction;
import swp.auctionkoi.models.User;
import swp.auctionkoi.repository.TransactionRepository;
import swp.auctionkoi.service.transaction.TransactionService;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('STAFF')")
public class TransactionServiceImpl implements TransactionService {


    TransactionRepository transactionRepository;


    public HashMap<Integer, Transaction> getAllTransaction(){
        HashMap<Integer, Transaction> transactions = new HashMap<>();
        List<Transaction> transactionList = transactionRepository.findAll();
        for (Transaction transaction : transactionList) {
            transactions.put(transaction.getId(), transaction);
        }
        return transactions;
    }

    public Optional<Transaction> getTransaction(int transactionId){
        if(transactionId <= 0){
            throw new AppException(ErrorCode.INVALID_TRANSACTION_ID);
        }
        Transaction transaction = transactionRepository.findById(transactionId).get();
        return Optional.ofNullable(transaction);
    }
}
