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



    }

