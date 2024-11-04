package swp.auctionkoi.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.respone.ApiResponse;
import swp.auctionkoi.models.Payment;
import swp.auctionkoi.service.payment.impl.PaymentServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {
    private final PaymentServiceImpl paymentService;

    @PostMapping("/withdraw/{userId}/{amount}")
    public ApiResponse<String> requestWithdraw(
            @PathVariable int userId,
            @PathVariable float amount) throws Exception {
        String result = paymentService.requestWithdrawMoney(userId, amount);
        return ApiResponse.<String>builder()
                .code(200)
                .message("Successfully")
                .result(result)
                .build();
    }

    @GetMapping("/pending-withdrawals")
    public ApiResponse<List<Payment>> getPendingWithdrawals() {
        List<Payment> pendingPayments = paymentService.getAllPendingWithdrawRequests();
        return ApiResponse.<List<Payment>>builder()
                .code(200)
                .message("Successfully")
                .result(pendingPayments)
                .build();
    }

    @PostMapping("/approve/{paymentId}")
    public ApiResponse<Void> approveWithdrawal(
            @PathVariable int paymentId) throws Exception {
        paymentService.approveWithdrawRequest(paymentId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Approved successfully!")
                .build();
    }

    @PostMapping("/reject/{paymentId}")
    public ApiResponse<Void> rejectWithdrawal(
            @PathVariable int paymentId) throws Exception {
        paymentService.rejectWithdrawRequest(paymentId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Rejected successfully!")
                .build();
    }
}
