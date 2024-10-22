//package swp.auctionkoi.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import swp.auctionkoi.dto.request.payment.WithdrawPaymentRequest;
//import swp.auctionkoi.exception.AppException;
//import swp.auctionkoi.service.VNPAYService;
//import swp.auctionkoi.service.payment.PaymentService;
//import swp.auctionkoi.service.payment.impl.PaymentServiceImpl;
//import swp.auctionkoi.service.transaction.TransactionService;
//import swp.auctionkoi.service.wallet.WalletService;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/wallet")
//public class WalletController {
//
//    @Autowired
//    private VNPAYService vnPayService;
//
//    @Autowired
//    private WalletService walletService;
//
//    @Autowired
//    private TransactionService transactionService;
//
//    @PostMapping("/topup")
//    public ResponseEntity<String> topUp(@RequestParam Double amount) {
//        String paymentUrl = vnPayService.createPayment(amount);
//        return ResponseEntity.status(HttpStatus.FOUND).header("Location", paymentUrl).build();
//    }
//
//    @GetMapping("/vnpay-return")
//    public ResponseEntity<String> vnpayReturn(@RequestParam Map<String, String> params) {
//        boolean isValid = vnPayService.validateResponse(params);
//        if (isValid) {
//            Double amount = Double.valueOf(params.get("vnp_Amount")) / 100;
//            Integer userId = Integer.parseInt(params.get("vnp_TxnRef")); // Lấy userId từ params nếu cần
//
//            walletService.updateWalletBalance(userId, amount);
//            transactionService.saveTransaction(userId, amount);
//
//            return ResponseEntity.ok("Nạp tiền thành công");
//        }
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nạp tiền thất bại");
//    }
//}
//
//
//
//
//
