package swp.auctionkoi.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import swp.auctionkoi.dto.respone.auction.*;
import swp.auctionkoi.service.AuctionNotificationService;

import java.util.List;

@Controller
public class WebSocketController {

    private final AuctionNotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketController(AuctionNotificationService notificationService, SimpMessagingTemplate messagingTemplate) {
        this.notificationService = notificationService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/request-pending")
    public void handlePendingRequest() {
        // Lấy các thông báo cũ từ AuctionNotificationService
        List<AuctionPendingInfo> pendingNotifications = notificationService.getPendingNotifications();

        // Gửi từng thông báo qua WebSocket đến frontend
        pendingNotifications.forEach(message ->
                messagingTemplate.convertAndSend("/auctions/pending", message)
        );

        List<AuctionStartInfo> startNotifications = notificationService.getStartNotifications();

        startNotifications.forEach(message ->
                messagingTemplate.convertAndSend("/auctions/start", message)
        );

        List<PlaceBidTraditionalInfo> placeBidNotifications = notificationService.getPlaceBidNotifications();

        placeBidNotifications.forEach(message ->
                messagingTemplate.convertAndSend("/auctions/place-bid/traditional", message)
        );

        List<UserWinAucionInfo> userWinNotifications = notificationService.getUserWinNotifications();

        userWinNotifications.forEach(message ->
                messagingTemplate.convertAndSend("/auctions/fixed-price/winner", message));

        List<AuctionCanNotStartInfo> canNotStartNotifications = notificationService.getCanNotStartNotifications();

        canNotStartNotifications.forEach(message ->
                messagingTemplate.convertAndSend("/auctions/not-start", message));

        List<AuctionEndInfo> notificationEnd = notificationService.getAuctionEndNotifications();

        notificationEnd.forEach(message ->
                messagingTemplate.convertAndSend("/auctions/end", message));
    }
}