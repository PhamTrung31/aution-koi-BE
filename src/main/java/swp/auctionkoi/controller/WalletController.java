package swp.auctionkoi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import swp.auctionkoi.dto.respone.ApiResponse;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.Wallet;
import swp.auctionkoi.service.wallet.impl.WalletServiceImpl;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    @Autowired
    private WalletServiceImpl walletService;

    @GetMapping("/{userId}")
    ApiResponse<Wallet> getWalletByUserId(@PathVariable int userId){

        Wallet wallet = walletService.getWalletByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_EXISTED));

        return ApiResponse.<Wallet>builder()
                .result(wallet)
                .code(200)
                .message("Successfully")
                .build();
    }
}
