package swp.auctionkoi.service.wallet;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.auctionkoi.models.User;
import swp.auctionkoi.models.Wallet;

import java.util.Optional;

public interface WalletService {
//    public Double getBalance(User user);
//    public void deduct(User user, Double amount);
//    public void refund(User user, Double amount);
//    public void transfer(User fromUser, User toUser, Double amount);


     public void updateWalletBalance(Integer userId, Double amount);
}
