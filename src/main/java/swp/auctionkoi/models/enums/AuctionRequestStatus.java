package swp.auctionkoi.models.enums;

public enum AuctionRequestStatus {
    WAIT,                // Yêu cầu chờ duyệt
    APPROVE,             // Đã được phê duyệt
    CANCEL,              // Đã bị hủy
    MANAGER_REVIEW,      // Đang được manager xem xét
    ASSIGNED_TO_STAFF,   // Giao lại cho staff để kiểm tra trực tiếp
    REJECTED              //HỦY BỞI MANAGER STAFF
}
