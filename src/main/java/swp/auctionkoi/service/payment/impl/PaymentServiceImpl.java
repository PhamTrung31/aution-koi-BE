package swp.auctionkoi.service.payment.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.Payment;
import swp.auctionkoi.models.Transaction;
import swp.auctionkoi.models.Wallet;
import swp.auctionkoi.models.User;
import swp.auctionkoi.repository.PaymentRepository;
import swp.auctionkoi.repository.TransactionRepository;
import swp.auctionkoi.repository.UserRepository;
import swp.auctionkoi.repository.WalletRepository;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.Payment;
import swp.auctionkoi.repository.PaymentRepository;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('STAFF')")
public class PaymentServiceImpl {

    PaymentRepository paymentRepository;
    TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;


    public HashMap<Integer, Payment> getAllPayment(){
        HashMap<Integer, Payment> payments = new HashMap<>();
        List<Payment> paymentList = paymentRepository.findAll();
        if(paymentList.isEmpty()){
            throw new AppException(ErrorCode.NO_PAYMENT);
        }
        for(Payment payment : paymentList){
            payments.put(payment.getId(), payment);
        }
        return payments;
    }


    public String withdrawMoney(int userId, float amount) throws Exception {
        // Check ví có tồn tại hay không
        Wallet wallet = walletRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_EXISTED));
        if(wallet == null)
            throw new AppException(ErrorCode.WALLET_NOT_EXISTED);
        //amount hợp lệ
        if(amount < 0 )
            throw new Exception("Invalid amount");
        // tiền rút phải nhỏ hơn hoặc bằng tiền đang có
        if(wallet.getBalance() <= amount)
            throw new Exception("Insufficient funds");

        // tạo cái payment
        Payment payment = new Payment();
        payment.setMember(wallet.getUser());
        payment.setAmount(amount);
        payment.setPaymentStatus(0);
        paymentRepository.save(payment);

        //update cái payment
        wallet.setBalance(wallet.getBalance() - amount);
        walletRepository.save(wallet);

        //tạo cái thông báo transaction

        Transaction transaction = new Transaction();
        transaction.setMember(wallet.getUser());
        transaction.setWalletId(wallet.getId());
        transaction.setPaymentId(payment.getId());
        transaction.setTransactionFee(0.0F);
        transaction.setTransactionDate(Instant.now());
        transaction.setAuction(null);
        transactionRepository.save(transaction);

        //cập nhật trạng thái thành công
        payment.setPaymentStatus(1); // 1 for success
        paymentRepository.save(payment);


        return "Withdraw successful";

    }



//    @Autowired
//    private WalletRepository walletRepository;
//    @Autowired
//    private UserRepository userRepository;
//
//    @Value("${vnpay.tmncode}")
//    private String vnp_TmnCode;
//
//    @Value("${vnpay.hashsecret}")
//    private String vnp_HashSecret;
//
//    @Value("${vnpay.url}")
//    private String vnp_Url;
//
//    @Value("${vnpay.returnurl}")
//    private String vnp_ReturnUrl;
//
//    // Nạp tiền vào ví sau khi thanh toán thành công
//    public void addMoneyToWallet(User member, Double amount) {
//        Wallet wallet = walletRepository.findByMember(member);
//        if (wallet != null) {
//            wallet.setBalance(wallet.getBalance() + amount);
//            walletRepository.save(wallet);  // Lưu lại thay đổi trong ví
//        } else {
//            // Nếu user chưa có ví, tạo ví mới
//            Wallet newWallet = new Wallet();
//            newWallet.setMember(member);
//            newWallet.setBalance(amount);
//            walletRepository.save(newWallet);
//        }
//    }

//    // Phương thức xử lý tạo thanh toán qua VNPay
//    public String createVnpayPayment(Integer userId, Double amount) {
//        // Tạo giao dịch VNPay như trước
//        String vnp_Version = "2.1.0";
//        String vnp_Command = "pay";
//        String txnRef = String.valueOf(System.currentTimeMillis());  // Mã giao dịch duy nhất
//        String vnp_OrderInfo = "Nap tien cho user " + userId;
//        String vnp_Amount = String.valueOf(amount * 100);  // Số tiền phải nhân với 100
//        String vnp_CreateDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
//        String vnp_IpAddr = "127.0.0.1";
//
//        Map<String, String> vnp_Params = new HashMap<>();
//        vnp_Params.put("vnp_Version", vnp_Version);
//        vnp_Params.put("vnp_Command", vnp_Command);
//        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
//        vnp_Params.put("vnp_Amount", vnp_Amount);
//        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
//        vnp_Params.put("vnp_CurrCode", "VND");
//        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
//        vnp_Params.put("vnp_Locale", "vn");
//        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
//        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
//        vnp_Params.put("vnp_TxnRef", txnRef);
//
//        // Xác thực chữ ký
//        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
//        Collections.sort(fieldNames);
//        StringBuilder hashData = new StringBuilder();
//        for (String fieldName : fieldNames) {
//            String fieldValue = vnp_Params.get(fieldName);
//            if ((fieldValue != null) && (fieldValue.length() > 0)) {
//                hashData.append(fieldName).append('=').append(fieldValue).append('&');
//            }
//        }
//        String queryUrl = hashData.substring(0, hashData.length() - 1);
//        String secureHash = hmacSHA512(vnp_HashSecret, queryUrl);
//        queryUrl += "&vnp_SecureHash=" + secureHash;
//
//        String paymentUrl = vnp_Url + "?" + queryUrl;
//        return paymentUrl;
//    }

//    // Phương thức xử lý phản hồi từ VNPay
//    public void handleVnpayReturn(Map<String, String> vnp_Params) {
//        String vnp_SecureHash = vnp_Params.remove("vnp_SecureHash");
//        String queryString = buildQueryString(vnp_Params);
//        String secureHashGenerated = hmacSHA512(vnp_HashSecret, queryString);
//
//        log.debug("Query string: {}", queryString);
//        log.debug("Generated SecureHash: {}", secureHashGenerated);
//        log.debug("Received SecureHash: {}", vnp_SecureHash);
//
//        if (secureHashGenerated.equals(vnp_SecureHash)) {
//            String responseCode = vnp_Params.get("vnp_ResponseCode");
//            if ("00".equals(responseCode)) {
//                // Giao dịch thành công, nạp tiền vào ví của user
//                Integer userId = Integer.valueOf(vnp_Params.get("vnp_OrderInfo").split(" ")[3]);
//                Double amount = Double.valueOf(vnp_Params.get("vnp_Amount")) / 100;
//                User user = userRepository.findById(userId)
//                        .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
//                addMoneyToWallet(user, amount);  // Nạp tiền vào ví của user
//            } else {
//                // Xử lý nếu giao dịch không thành công
//                throw new RuntimeException("Giao dịch thất bại với mã: " + responseCode);
//            }
//        } else {
//            throw new RuntimeException("Chữ ký không hợp lệ");
//        }
//    }
//
//    private String buildQueryString(Map<String, String> params) {
//        List<String> fieldNames = new ArrayList<>(params.keySet());
//        Collections.sort(fieldNames);
//        StringBuilder queryString = new StringBuilder();
//        for (String fieldName : fieldNames) {
//            String fieldValue = params.get(fieldName);
//            if ((fieldValue != null) && (fieldValue.length() > 0)) {
//                queryString.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8))
//                        .append('=')
//                        .append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8))
//                        .append('&');
//            }
//        }
//        if (queryString.length() > 0) {
//            queryString.setLength(queryString.length() - 1); // Remove the last '&'
//        }
//        return queryString.toString();
//    }
//
//    private String hmacSHA512(String key, String data) {
//        try {
//            Mac hmac512 = Mac.getInstance("HmacSHA512");
//            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA512");
//            hmac512.init(secretKey);
//            byte[] hash = hmac512.doFinal(data.getBytes());
//            return Hex.encodeHexString(hash);
//        } catch (Exception e) {
//            throw new RuntimeException("Error while signing request", e);
//        }
//    }
}
