package swp.auctionkoi.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<String> paymentCompleted(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Gọi phương thức xử lý thanh toán từ service
            vnPayService.orderReturn(request, response);

            // Các thông tin giao dịch từ request
            String memberId = request.getParameter("vnp_OrderInfo");
            String paymentTime = request.getParameter("vnp_PayDate");
            String transactionId = request.getParameter("vnp_TransactionNo");
            String totalPrice = request.getParameter("vnp_Amount");

            // Kiểm tra các giá trị cần thiết
            if (memberId == null || paymentTime == null || transactionId == null || totalPrice == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing required parameters");
            }

            System.out.println("Member ID: " + memberId);
            System.out.println("Payment Time: " + paymentTime);
            System.out.println("Transaction ID: " + transactionId);
            System.out.println("Total Price: " + totalPrice);

            // Chuyển đổi số tiền từ chuỗi thành số và chia cho 100
            float amount = Float.valueOf(totalPrice) / 100;

            // Tìm kiếm ví của thành viên
            Wallet wallet = walletRepository.findByUserId(Integer.valueOf(memberId))
                    .orElseThrow(() -> new RuntimeException("Wallet not found"));

            // Kiểm tra trạng thái thanh toán
            if ("00".equals(request.getParameter("vnp_TransactionStatus"))) { // Giao dịch thành công
                // Cập nhật số dư trong ví
                wallet.setBalance(wallet.getBalance() + amount);
                walletRepository.save(wallet);

                // Tạo giao dịch mới
                Transaction transaction = new Transaction();
                transaction.setMember(wallet.getMember());
                transaction.setWalletId(wallet.getId());
                transaction.setTransactionType(TransactionType.TOP_UP);
                transaction.setAmount(amount);
                transaction.setTransactionFee(0.0F); // Không có phí giao dịch
                transaction.setPaymentId(Integer.valueOf(transactionId));

                transactionRepository.save(transaction);

                // Trả về thông báo thành công
                return ResponseEntity.ok("Payment successful");

            } else { // Giao dịch thất bại
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment failed");
            }

        } catch (Exception e) {
            // Xử lý lỗi chung
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

}

