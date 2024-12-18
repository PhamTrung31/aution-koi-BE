package swp.auctionkoi.service.wallet;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.auctionkoi.models.User;
import swp.auctionkoi.models.Wallet;

import java.util.Optional;

import org.springframework.stereotype.Service;
import swp.auctionkoi.models.Wallet;

import java.util.Optional;

public interface WalletService {
    public Optional<Wallet> getWalletByUserId(int userId);
}
