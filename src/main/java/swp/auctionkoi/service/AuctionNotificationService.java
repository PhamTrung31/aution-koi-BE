package swp.auctionkoi.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.respone.auction.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuctionNotificationService {

    SimpMessagingTemplate messagingTemplate;

    // Tạo danh sách tạm thời để lưu thông báo
    private final List<AuctionPendingInfo> pendingNotifications = new ArrayList<>();
    private final List<AuctionStartInfo> startNotifications = new ArrayList<>();

    public void sendAuctionPendingNotification(AuctionPendingInfo notificationPendingInfo) {
        log.info("Auction pending send noti was run");

        // Lưu thông báo vào danh sách tạm
        if (pendingNotifications.size() >= 1) { // Giới hạn số lượng
            pendingNotifications.remove(0);
        }
        pendingNotifications.add(notificationPendingInfo);
        log.info("List pending message: " + pendingNotifications);

        messagingTemplate.convertAndSend("/auctions/pending", notificationPendingInfo);
    }

    public List<AuctionPendingInfo> getPendingNotifications() {
        return new ArrayList<>(pendingNotifications);  // Trả về bản sao để đảm bảo an toàn dữ liệu
    }

    public void sendAuctionStartNotification(AuctionStartInfo notificationStart) {
        log.info("Auction start send noti was run");

        if (startNotifications.size() >= 1) { // Giới hạn số lượng
            startNotifications.remove(0);
        }
        startNotifications.add(notificationStart);
        log.info("List pending message: " + startNotifications);

        messagingTemplate.convertAndSend("/auctions/start", notificationStart);
    }

    public List<AuctionStartInfo> getStartNotifications() {
        return new ArrayList<>(startNotifications);  // Trả về bản sao để đảm bảo an toàn dữ liệu
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
        // them list bid cua auction
    }
}
