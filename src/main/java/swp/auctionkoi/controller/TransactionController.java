package swp.auctionkoi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.respone.ApiResponse;
import swp.auctionkoi.models.Transaction;
import swp.auctionkoi.service.transaction.MemberTransactionService;
import swp.auctionkoi.service.transaction.TransactionService;
import swp.auctionkoi.service.transaction.impl.TransactionServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionServiceImpl transactionService;

    @Autowired
    private MemberTransactionService memberTransactionService;

    // API cho phép sắp xếp theo cột chỉ định
    @GetMapping("/sort")
    public List<Transaction> getTransactionsSorted(
            @RequestParam(defaultValue = "transactionDate") String sortBy,  // Cột mặc định
            @RequestParam(defaultValue = "asc") String order  // Kiểu sắp xếp mặc định
    ) {
        return transactionService.getSortedTransactions(sortBy, order);
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<Transaction>> getTransactionsByUserId(@PathVariable Integer userId) {
        List<Transaction> transactions = transactionService.getTransactionsByUserId(userId);

        return ApiResponse.<List<Transaction>>builder()
                .code(200)
                .message("Successfully")
                .result(transactions)
                .build();
    }

    @GetMapping("/all/{userId}")
    public ApiResponse<List<Transaction>> getAllTransactionsOfUser(@PathVariable Integer userId) {
        return ApiResponse.<List<Transaction>>builder()
                .code(200)
                .result(memberTransactionService.getAllTransactionsByUserId(userId))
                .build();
    }

    @GetMapping("/all")
    public ApiResponse<List<Transaction>> getAllTransactions() {
        return ApiResponse.<List<Transaction>>builder()
                .code(200)
                .message("Successfully")
                .result(transactionService.getAllTransaction())
                .build();
    }
}

