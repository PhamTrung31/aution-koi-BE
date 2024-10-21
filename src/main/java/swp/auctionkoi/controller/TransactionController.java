//package swp.auctionkoi.controller;
//
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import swp.auctionkoi.dto.respone.ApiResponse;
//import swp.auctionkoi.models.Transaction;
//import swp.auctionkoi.service.transaction.impl.TransactionServiceImpl;
//
//import java.util.List;
//
//@Slf4j
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/transactions")
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
////public class TransactionController {
////
////    TransactionServiceImpl transactionService;
////
////    @GetMapping("/sort")
////    public ApiResponse<List<Transaction>> getSortedTransactions(@RequestParam String sortBy,
////                                                                @RequestParam String sortDir) {
////
////            List<Transaction> sortedTransactions = transactionService.getSortedTransactions(sortBy, sortDir);
////            return ApiResponse.<List<Transaction>>builder()
////                    .result(sortedTransactions)
////                    .code(200)
////                    .message("Successfully")
////                    .build();
////
////    }
//
//
//}
