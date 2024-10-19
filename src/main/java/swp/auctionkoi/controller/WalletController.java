package swp.auctionkoi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.request.payment.WithdrawPaymentRequest;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.service.payment.PaymentService;
import swp.auctionkoi.service.payment.impl.PaymentServiceImpl;

@RestController
@RequestMapping("/api/wallet")

public class WalletController {


}




