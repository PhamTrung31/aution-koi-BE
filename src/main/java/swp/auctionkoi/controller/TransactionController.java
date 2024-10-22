//package swp.auctionkoi.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import swp.auctionkoi.models.Transaction;
//import swp.auctionkoi.service.transaction.TransactionService;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/transactions")
//public class TransactionController {
//
//    @Autowired
//    private TransactionService transactionService;
//
//    // API cho phép sắp xếp theo cột chỉ định
//    @GetMapping("/sort")
//    public List<Transaction> getTransactionsSorted(
//            @RequestParam(defaultValue = "transactionDate") String sortBy,  // Cột mặc định
//            @RequestParam(defaultValue = "asc") String order  // Kiểu sắp xếp mặc định
//    ) {
//        return transactionService.getSortedTransactions(sortBy, order);
//    }
//}
//
