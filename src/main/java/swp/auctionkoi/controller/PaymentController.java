package swp.auctionkoi.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.respone.ApiResponse;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.Payment;
import swp.auctionkoi.models.User;
import swp.auctionkoi.models.enums.Role;
import swp.auctionkoi.repository.UserRepository;
import swp.auctionkoi.service.payment.impl.PaymentServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {
    PaymentServiceImpl paymentService;

    UserRepository userRepository;

    private User getUserFromContext(){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        return userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    @PostMapping("/withdraw/{amount}")
    public ApiResponse<String> requestWithdraw(
            @PathVariable float amount) throws Exception {

        User user = getUserFromContext();

        if(!user.getRole().equals(Role.MEMBER)){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        String result = paymentService.requestWithdrawMoney(user.getId(), amount);
        return ApiResponse.<String>builder()
                .code(200)
                .message("Successfully")
                .result(result)
                .build();
    }
    @GetMapping("/all/payment")
    public ApiResponse<List<Payment>> getAllPaymentsOfBreeder() {

        User breeder = getUserFromContext();

        if(!breeder.getRole().equals(Role.BREEDER)){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        List<Payment> payments = paymentService.getAllPayMentOfBreeder(breeder.getId());
       return ApiResponse.<List<Payment>>builder()
               .code(200)
               .message("Successfully")
               .result(payments)
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
