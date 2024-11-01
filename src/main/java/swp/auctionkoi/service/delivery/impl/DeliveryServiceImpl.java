package swp.auctionkoi.service.delivery.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.*;
import swp.auctionkoi.models.enums.DeliveryStatus;
import swp.auctionkoi.models.enums.Role;
import swp.auctionkoi.models.enums.TransactionType;
import swp.auctionkoi.repository.*;
import swp.auctionkoi.service.delivery.DeliveryService;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeliveryServiceImpl implements DeliveryService {

    DeliveryRepository deliveryRepository;
    WalletRepository walletRepository;
    TransactionRepository transactionRepository;
    BidRepository bidRepository;
    UserRepository userRepository;
    AuctionRequestRepository auctionRequestRepository;

    @Override
    public void updateDeliveryStatus(int deliveryId, DeliveryStatus status) {
        // Log the received inputs
        System.out.println("Updating delivery ID: " + deliveryId + " to status: " + status);
        // Find the delivery by ID
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new AppException(ErrorCode.DELIVERY_NOT_EXISTED));

        // Log current delivery status
        System.out.println("Current status of delivery ID " + deliveryId + " is: " + delivery.getDeliveryStatus());


        // Update the delivery status
        delivery.setDeliveryStatus(status);
        deliveryRepository.save(delivery);

        // If delivery is successful, transfer the money to the breeder
        if (status == DeliveryStatus.DELIVERY_SUCCESS) {
            Auction auction = delivery.getTransaction().getAuction();
            User winner = auction.getWinner();

            // Get the AuctionRequest for the current auction
            AuctionRequest auctionRequest = auctionRequestRepository.findByAuctionId(auction.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.AUCTION_NOT_EXISTED));

            // Get the breeder from the AuctionRequest
            User breeder = auctionRequest.getUser();
            Wallet breederWallet = walletRepository.findByUserId(breeder.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_EXISTED));

            // Get admin wallet and info
            User admin = (User) userRepository.findFirstByRole(Role.MANAGER)
                    .orElseThrow(() -> new AppException(ErrorCode.ADMIN_NOT_FOUND));

            Wallet adminWallet = walletRepository.findByUserId(admin.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_EXISTED));

            // Get the highest bid from the winner
            Bid bid = bidRepository.findByAuctionIdAndUserId(auction.getId(), winner.getId());
            if (bid != null) {
                float winningBid = bid.getBidAmount();

                // Calculate the breeder's amount (after 10% fee)
                float amountForBreeder = winningBid * 0.90F;
                adminWallet.setBalance(adminWallet.getBalance() - amountForBreeder);
                breederWallet.setBalance(breederWallet.getBalance() + amountForBreeder);
                walletRepository.save(adminWallet);
                walletRepository.save(breederWallet);

                // Log the transfer transaction
                Transaction paymentTransaction = Transaction.builder()
                        .user(breeder)
                        .auction(auction)
                        .transactionType(TransactionType.TRANSFER)
                        .walletId(breederWallet.getId())
                        .transactionFee(amountForBreeder)
                        .build();
                transactionRepository.save(paymentTransaction);
            }
        }
    }
}

