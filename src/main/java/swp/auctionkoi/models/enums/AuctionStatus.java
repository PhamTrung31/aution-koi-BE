package swp.auctionkoi.models.enums;

public enum AuctionStatus {
    NEW,                // 0 - Đấu giá mới tạo
    PENDING,            // 1 - Đang chờ bắt đầu (chưa đủ người tham gia hoặc chờ thời gian bắt đầu)
    IN_PROGRESS,        // 2 - Đấu giá đang diễn ra
    BUYOUT_TRIGGERED,    // 3 - Có người ra giá bằng với giá mua ngay
    COMPLETED,          // 4 - Đấu giá đã hoàn tất và có người thắng cuộc
    CANCELED,           // 5 - Đấu giá bị hủy
    UNSOLD,             // 6 - Không có ai thắng đấu giá hoặc không có ai tham gia
    DELIVERED           // 7 - Cá đã được giao cho người thắng cuộc
}
