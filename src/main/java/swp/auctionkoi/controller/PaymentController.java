package swp.auctionkoi.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import swp.auctionkoi.configuration.SecurityConfig;
import swp.auctionkoi.dto.request.payment.WithdrawPaymentRequest;
import swp.auctionkoi.dto.respone.ApiResponse;
import swp.auctionkoi.models.User;
import swp.auctionkoi.service.payment.impl.PaymentServiceImpl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/payment")
@Controller
public class PaymentController {

    @Autowired
    private PaymentServiceImpl paymentService;

    @PostMapping("/requestwithdraw")
    public ApiResponse<String> requestWithdrawMoney(@RequestBody WithdrawPaymentRequest request) throws Exception {
        Integer userId = request.getUserId();
        Float amount = request.getAmount();

        // Call withdraw method
        String result = paymentService.requestWithdrawMoney(userId, amount);
        return ApiResponse.<String>builder()
                .result(result)
                .code(200)
                .message("Successfully")
                .build();
    }

//    @GetMapping("/vnpay-return")
//    public ResponseEntity<String> vnpayReturn(HttpServletRequest request) {
//        Map<String, String> vnp_Params = new HashMap<>();
//        Map<String, String[]> params = request.getParameterMap();
//
//        // Lấy các tham số từ request
//        for (Map.Entry<String, String[]> entry : params.entrySet()) {
//            String key = entry.getKey();
//            String[] values = entry.getValue();
//            vnp_Params.put(key, values[0]);
//        }
//
//        // Lấy SecureHash từ phản hồi của VNPAY và loại bỏ nó khỏi danh sách tham số
//        String vnpSecureHash = vnp_Params.remove("vnp_SecureHash");
//
//        // Sắp xếp các trường theo thứ tự alphabet (trừ vnp_SecureHash)
//        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
//        Collections.sort(fieldNames);
//
//        // Tạo chuỗi dữ liệu để mã hóa lại chữ ký (hashData)
//        StringBuilder hashData = new StringBuilder();
//        for (String fieldName : fieldNames) {
//            String fieldValue = vnp_Params.get(fieldName);
//            if (fieldValue != null && fieldValue.length() > 0) {
//                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII)).append('&');
//            }
//        }
//
//        // Xóa ký tự '&' cuối cùng
//        if (hashData.length() > 0) {
//            hashData.deleteCharAt(hashData.length() - 1);
//        }
//
//        // Tính toán lại SecureHash từ dữ liệu nhận được
//        String generatedSecureHash = SecurityConfig.hmacSHA512(SecurityConfig.secretKey, hashData.toString());
//
//        // In ra SecureHash đã tạo và SecureHash nhận được từ VNPAY để kiểm tra
//        System.out.println("Generated SecureHash: " + generatedSecureHash);
//        System.out.println("Received SecureHash: " + vnpSecureHash);
//
//        // Kiểm tra SecureHash nhận được từ VNPAY có khớp với cái mà bạn tự tạo hay không
//        if (!generatedSecureHash.equals(vnpSecureHash)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid secure hash");
//        }
//
//        // Nếu SecureHash hợp lệ, kiểm tra tiếp các mã trạng thái phản hồi
//        String responseCode = vnp_Params.get("vnp_ResponseCode");
//        String transactionStatus = vnp_Params.get("vnp_TransactionStatus");
//
//        System.out.println(" responseCode: " + responseCode);
//        System.out.println(" transactionStatus: " + transactionStatus);
//
//
//        if ("00".equals(responseCode) && "00".equals(transactionStatus)) {
//            // Giao dịch thành công
//
//                paymentService.handleVnpayReturn(vnp_Params);  // Gọi service xử lý giao dịch thành công
//                return ResponseEntity.status(HttpStatus.FOUND)
//                        .header("Location", "/payment/success")
//                        .build();
//
//
//        } else {
//            // Giao dịch thất bại
//            return ResponseEntity.status(HttpStatus.FOUND)
//                    .header("Location", "/payment/fail")
//                    .build();
//        }
//    }


//    @GetMapping("/vnpay-return")
//    public ResponseEntity<String> vnpayReturn(HttpServletRequest request) {
//        try {
//            // Existing code...
//            Map<String, String> vnp_Params = new HashMap<>();
//            Map<String, String[]> params = request.getParameterMap();
//
//            // Lấy các tham số từ request
//            for (Map.Entry<String, String[]> entry : params.entrySet()) {
//                String key = entry.getKey();
//                String[] values = entry.getValue();
//                vnp_Params.put(key, values[0]);
//            }
//
//            // Lấy SecureHash từ phản hồi của VNPAY và loại bỏ nó khỏi danh sách tham số
//            String vnpSecureHash = vnp_Params.remove("vnp_SecureHash");
//
//            // Sắp xếp các trường theo thứ tự alphabet (trừ vnp_SecureHash)
//            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
//            Collections.sort(fieldNames);
//
//            // Tạo chuỗi dữ liệu để mã hóa lại chữ ký (hashData)
//            StringBuilder hashData = new StringBuilder();
//            for (String fieldName : fieldNames) {
//                String fieldValue = vnp_Params.get(fieldName);
//                if (fieldValue != null && fieldValue.length() > 0) {
//                    hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII)).append('&');
//                }
//            }
//
//            // Xóa ký tự '&' cuối cùng
//            if (hashData.length() > 0) {
//                hashData.deleteCharAt(hashData.length() - 1);
//            }
//
//            // Tính toán lại SecureHash từ dữ liệu nhận được
//            String generatedSecureHash = SecurityConfig.hmacSHA512(SecurityConfig.secretKey, hashData.toString());
//
//            // In ra SecureHash đã tạo và SecureHash nhận được từ VNPAY để kiểm tra
//            System.out.println("Generated SecureHash: " + generatedSecureHash);
//            System.out.println("Received SecureHash: " + vnpSecureHash);
//
//            // Kiểm tra SecureHash nhận được từ VNPAY có khớp với cái mà bạn tự tạo hay không
//            if (!generatedSecureHash.equals(vnpSecureHash)) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid secure hash");
//            }
//
//            // Nếu SecureHash hợp lệ, kiểm tra tiếp các mã trạng thái phản hồi
//            String responseCode = vnp_Params.get("vnp_ResponseCode");
//            String transactionStatus = vnp_Params.get("vnp_TransactionStatus");
//
//            System.out.println(" responseCode: " + responseCode);
//            System.out.println(" transactionStatus: " + transactionStatus);
//            log.info("VNPAY return - Parameters: {}", vnp_Params);
//
//            if ("00".equals(responseCode) && "00".equals(transactionStatus)) {
//                log.info("Transaction successful, calling handleVnpayReturn");
//                paymentService.handleVnpayReturn(vnp_Params);
//                log.info("handleVnpayReturn completed successfully");
//                return ResponseEntity.status(HttpStatus.FOUND)
//                        .header("Location", "/payment/success")
//                        .build();
//            } else {
//                log.info("Transaction failed, redirecting to failure page");
//                return ResponseEntity.status(HttpStatus.FOUND)
//                        .header("Location", "/payment/fail")
//                        .build();
//            }
//        } catch (Exception e) {
//            log.error("Error in vnpayReturn", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
//        }
//    }
//
//
//
//
//
//
//    @GetMapping("/payment/success")
//            public String paymentSuccess() {
//                return "success";  // Tên của file HTML thành công
//            }
//
//            @GetMapping("/payment/fail")
//            public String paymentFail() {
//                return "fail";  // Tên của file HTML thất bại
//            }


    }

