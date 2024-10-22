package swp.auctionkoi.service.wallet.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp.auctionkoi.models.User;
import swp.auctionkoi.models.Wallet;
import swp.auctionkoi.repository.UserRepository;
import swp.auctionkoi.repository.WalletRepository;
import swp.auctionkoi.service.wallet.WalletService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WalletServiceImpl implements WalletService {


    @Autowired
    WalletRepository walletRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public void updateWalletBalance(Integer userId, Double amount) {
        // Tìm ví của người dùng dựa trên userId
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        if (wallet != null) {
            // Cập nhật số dư
            wallet.setBalance(wallet.getBalance() + amount);
            walletRepository.save(wallet);
        } else {
            throw new RuntimeException("Không tìm thấy ví của người dùng này.");
        }
    }
}
