package swp.auctionkoi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import swp.auctionkoi.dto.respone.ApiResponse;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.User;
import swp.auctionkoi.models.Wallet;
import swp.auctionkoi.models.enums.Role;
import swp.auctionkoi.repository.UserRepository;
import swp.auctionkoi.service.wallet.impl.WalletServiceImpl;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    @Autowired
    private WalletServiceImpl walletService;
    @Autowired
    private UserRepository userRepository;

    private User getUserFromContext(){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        return userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    @GetMapping()
    ApiResponse<Wallet> getWalletByUserId(){

        User user = getUserFromContext();

        if(!user.getRole().equals(Role.MEMBER)){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Wallet wallet = walletService.getWalletByUserId(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_EXISTED));

        return ApiResponse.<Wallet>builder()
                .result(wallet)
                .code(200)
                .message("Successfully")
                .build();
    }
}
