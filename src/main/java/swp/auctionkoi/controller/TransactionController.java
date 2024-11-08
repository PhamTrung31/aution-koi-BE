package swp.auctionkoi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.respone.ApiResponse;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.Transaction;
import swp.auctionkoi.models.User;
import swp.auctionkoi.models.enums.Role;
import swp.auctionkoi.repository.UserRepository;
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
    @Autowired
    private UserRepository userRepository;


    private User getUserFromContext(){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        return userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }


    // API cho phép sắp xếp theo cột chỉ định
    @GetMapping("/sort")
    public List<Transaction> getTransactionsSorted(
            @RequestParam(defaultValue = "transactionDate") String sortBy,  // Cột mặc định
            @RequestParam(defaultValue = "asc") String order  // Kiểu sắp xếp mặc định
    ) {
        return transactionService.getSortedTransactions(sortBy, order);
    }

    @GetMapping("/transaction")
    public ApiResponse<List<Transaction>> getTransactionsByUserId(@PathVariable Integer userId) {
        List<Transaction> transactions = transactionService.getTransactionsByUserId(userId);

        return ApiResponse.<List<Transaction>>builder()
                .code(200)
                .message("Successfully")
                .result(transactions)
                .build();
    }

    @GetMapping("/transaction/all")
    public ApiResponse<List<Transaction>> getAllTransactionsOfUser() { //for user use

        User user = getUserFromContext();

        if(!user.getRole().equals(Role.MEMBER)){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return ApiResponse.<List<Transaction>>builder()
                .code(200)
                .result(memberTransactionService.getAllTransactionsByUserId(user.getId()))
                .build();
    }
}

