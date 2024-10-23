package swp.auctionkoi.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.OrderRequestDTO;
import swp.auctionkoi.models.Transaction;
import swp.auctionkoi.models.Wallet;
import swp.auctionkoi.models.enums.TransactionType;
import swp.auctionkoi.repository.TransactionRepository;
import swp.auctionkoi.repository.WalletRepository;
import swp.auctionkoi.service.VNPAYService;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/vnpay")
public class VNPAYController {

    @Autowired
    private VNPAYService vnPayService;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping({"", "/"})
    public String home() {
        return "createOrder";
    }


    @PostMapping("/submitOrder")
    public ResponseEntity<Map<String, String>> submitOrder(@RequestBody OrderRequestDTO orderRequestDTO, HttpServletRequest request) {
        // Lấy dữ liệu từ DTO
        long orderTotal = orderRequestDTO.getAmount();
        String memberId = orderRequestDTO.getMemberId();

        // Loại bỏ các ký tự không mong muốn khỏi memberId
        memberId = memberId.replaceAll("\\s+", "").replaceAll("[^a-zA-Z0-9]", "").trim();

        // Kiểm tra xem memberId có hợp lệ không
        if (memberId.isEmpty()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Member ID không hợp lệ.");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Tạo URL VNPay
        String baseUrl = request.getScheme() + "://" + request.getServerName();
        if (request.getServerPort() != 80 && request.getServerPort() != 443) {
            baseUrl += ":" + request.getServerPort();
        }

        // Loại bỏ newline và ký tự không hợp lệ khỏi baseUrl
        baseUrl = baseUrl.replaceAll("[\\n\\r]", "").replace("%0A", "").trim();

        // Gọi hàm createOrder() để lấy URL thanh toán
        String vnpayUrl = vnPayService.createOrder(request, orderTotal, memberId, baseUrl);

        // Tạo response dưới dạng JSON với Map
        Map<String, String> response = new HashMap<>();
        response.put("vnpayUrl", vnpayUrl);  // Trả về URL thanh toán cho client

        // Trả về JSON hợp lệ
        return ResponseEntity.ok(response);
    }








    // Sau khi hoàn tất thanh toán, VNPAY sẽ chuyển hướng trình duyệt về URL này
@GetMapping("/vnpay-payment-return")
public String paymentCompleted(HttpServletRequest request, Model model) {
    int paymentStatus = vnPayService.orderReturn(request);
    System.out.println("Payment Status: " + paymentStatus);

    String memberId = request.getParameter("vnp_OrderInfo");
    String paymentTime = request.getParameter("vnp_PayDate");
    String transactionId = request.getParameter("vnp_TransactionNo");
    String totalPrice = request.getParameter("vnp_Amount");
    System.out.println("Member ID: " + memberId);
    System.out.println("Payment Time: " + paymentTime);
    System.out.println("Transaction ID: " + transactionId);
    System.out.println("Total Price: " + totalPrice);

    Double amount = Double.valueOf(totalPrice) / 100;  // Chia giá trị cho 100

    if (paymentStatus == 1) {
        Wallet wallet = walletRepository.findByUserId(Integer.valueOf(memberId))
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        if (wallet != null) {
            wallet.setBalance(wallet.getBalance() + amount);
            walletRepository.save(wallet);

            Transaction transaction = new Transaction();
            transaction.setMember(wallet.getMember());
            transaction.setWalletId(wallet.getId());
            transaction.setTransactionType(TransactionType.TOP_UP);
            transaction.setTransactionFee(0.0);
            transaction.setPaymentId(Integer.valueOf(transactionId));

            transactionRepository.save(transaction);
        }

        model.addAttribute("orderId", memberId);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("paymentTime", paymentTime);
        model.addAttribute("transactionId", transactionId);

        return "orderSuccess";
    } else {
        return "orderFail";
    }
}
}
