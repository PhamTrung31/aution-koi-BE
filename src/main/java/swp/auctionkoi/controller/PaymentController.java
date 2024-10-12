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
import swp.auctionkoi.models.User;
import swp.auctionkoi.service.payment.impl.PaymentServiceImpl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/payment")
@Controller
public class PaymentController {

    @Autowired
    private PaymentServiceImpl paymentService;

    @GetMapping("/pay")
    public String getPay() throws UnsupportedEncodingException{

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        long amount = 10000*100;
        String bankCode = "NCB";

        String vnp_TxnRef = SecurityConfig.getRandomNumber(8);
        String vnp_IpAddr = "127.0.0.1";

        String vnp_TmnCode = SecurityConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        vnp_Params.put("vnp_BankCode", bankCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);

        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", SecurityConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = SecurityConfig.hmacSHA512(SecurityConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = SecurityConfig.vnp_PayUrl + "?" + queryUrl;


        return paymentUrl;
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

