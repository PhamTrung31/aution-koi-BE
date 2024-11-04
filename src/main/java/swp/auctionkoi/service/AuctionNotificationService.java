package swp.auctionkoi.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.respone.auction.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuctionNotificationService {

    SimpMessagingTemplate messagingTemplate;



    public void sendAuctionPendingNotification(AuctionPendingInfo notificationPendingInfo) {
        log.info("Auction pending send noti was run");
        messagingTemplate.convertAndSend("/auctions/pending", notificationPendingInfo);
    }

    public void sendAuctionStartNotification(AuctionStartInfo notificationStart) {
        log.info("Auction start send noti was run");
        messagingTemplate.convertAndSend("/auctions/start", notificationStart);
    }

    public void sendPlaceBidTraditionalNotification(PlaceBidTraditionalInfo placeBidTraditionalInfo){
        log.info("Place bid traditional send noti was run");
        messagingTemplate.convertAndSend("/auctions/place-bid/traditional", placeBidTraditionalInfo);
    }

    public void sendWinnerOfFixedPrice(UserWinAucionInfo userWinAucionInfo){
        log.info("Winner of fixed price send noti was run");
        messagingTemplate.convertAndSend("/auctions/fixed-price/winner", userWinAucionInfo);
    }

    public void sendAuctionCantNotStartNotification(AuctionCanNotStartInfo notificationCanNotStart) {
        log.info("Auction can not start send noti was run");
        messagingTemplate.convertAndSend("/auctions/not-start", notificationCanNotStart);
    }


    public void sendAuctionEndNotification(AuctionEndInfo notificationEnd) {
        log.info("Auction end send noti was run");
        messagingTemplate.convertAndSend("/auctions/end", notificationEnd);
    }
}
