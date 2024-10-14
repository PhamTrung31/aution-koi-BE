package swp.auctionkoi.service.wallet;

import org.springframework.stereotype.Service;
import swp.auctionkoi.models.Wallet;

import java.util.Optional;

public interface WalletService {
    public Optional<Wallet> getWalletByUserId(int userId);
}
