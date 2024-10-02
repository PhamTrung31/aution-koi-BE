package swp.auctionkoi.service.wallet.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import swp.auctionkoi.models.User;
import swp.auctionkoi.models.Wallet;
import swp.auctionkoi.repository.WalletRepository;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WalletServiceImpl {

    WalletRepository walletRepository;

    public int findByUserId(int userId){
        Wallet wallet = walletRepository.findById(userId).orElse(null);
        if(wallet != null){

        }
        return userId;
    }
}
