package swp.auctionkoi.service.wallet.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.Wallet;
import swp.auctionkoi.repository.WalletRepository;
import swp.auctionkoi.service.wallet.WalletService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WalletServiceImpl implements WalletService {
    WalletRepository walletRepository;

    public Optional<Wallet> getWalletByUserId(int userId) {
        if (userId <= 0) {
            throw new AppException(ErrorCode.INVALID_USER_ID);
        }
        return Optional.ofNullable(walletRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }



}
