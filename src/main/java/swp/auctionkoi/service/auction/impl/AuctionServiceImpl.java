package swp.auctionkoi.service.auction.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp.auctionkoi.models.Auction;
import swp.auctionkoi.models.Transaction;
import swp.auctionkoi.models.User;
import swp.auctionkoi.models.Wallet;
import swp.auctionkoi.repository.AuctionRepository;
import swp.auctionkoi.repository.TransactionRepository;
import swp.auctionkoi.repository.UserRepository;
import swp.auctionkoi.repository.WalletRepository;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AuctionServiceImpl {
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    public void joinAuctionWithDeposit(int auctionId, int memberId, double depositAmount) {
        User user = userRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        Wallet wallet = walletRepository.findByMember_Id(memberId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));


        wallet.setBalance(wallet.getBalance() - depositAmount);
        walletRepository.save(wallet);

        Transaction transaction = new Transaction();
        transaction.setMember(user);
        transaction.setAuction(auction);
        transaction.setTransactionFee(depositAmount);
        transaction.setTransactionDate(Instant.now());
        transactionRepository.save(transaction);
    }

    public void handleAuctionResult(int auctionId, int winningMemberId) {
        Auction auction1 = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        // Find all deposit transactions for the auction
        List<Transaction> depositTransactions = transactionRepository.findByAuctionId(auctionId);

        for (Transaction transaction : depositTransactions) {
            if (!transaction.getMember().getId().equals(winningMemberId)) {
                // Refund the deposit if the member lost the auction
                Wallet wallet = walletRepository.findById(transaction.getMember().getId())
                        .orElseThrow(() -> new RuntimeException("Wallet not found"));
                Double depositAmount = transaction.getTransactionFee();
                wallet.setBalance(wallet.getBalance() + depositAmount);
                walletRepository.save(wallet);

                // Mark the deposit as refunded

                transaction.setTransactionFee(depositAmount);
                transactionRepository.save(transaction);
            }
        }
    }

    public Optional<Auction> getAuction(int id) {
        return auctionRepository.findById(id);
    }


    public Auction addAuction(Auction auction) {
        return auctionRepository.save(auction);
    }


    public Auction updateAuction(Auction auction) {
        Optional<Auction> existingAuction = auctionRepository.findById(auction.getId());
        if (existingAuction.isPresent()) {
            return auctionRepository.save(auction);
        }
        throw new RuntimeException("Auction not found");
    }


    public void deleteAuction(int id) {
        Optional<Auction> existingAuction = auctionRepository.findById(id);
        if (existingAuction.isPresent()) {
            auctionRepository.deleteById(id);
        } else {
            throw new RuntimeException("Auction not found");
        }
    }
}
