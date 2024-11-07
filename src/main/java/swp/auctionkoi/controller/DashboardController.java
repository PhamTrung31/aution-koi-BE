package swp.auctionkoi.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swp.auctionkoi.dto.respone.ApiResponse;
import swp.auctionkoi.dto.respone.DashboardResponse;
import swp.auctionkoi.dto.respone.MonthlyAuctionDataResponse;
import swp.auctionkoi.models.User;
import swp.auctionkoi.models.Wallet;
import swp.auctionkoi.models.enums.AuctionStatus;
import swp.auctionkoi.models.enums.Role;
import swp.auctionkoi.repository.AuctionRepository;
import swp.auctionkoi.repository.AuctionRequestRepository;
import swp.auctionkoi.repository.UserRepository;
import swp.auctionkoi.repository.WalletRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DashboardController {
    UserRepository userRepository;
    AuctionRepository auctionRepository;
    WalletRepository walletRepository;
    AuctionRequestRepository auctionRequestRepository;

    @GetMapping
    public ApiResponse<DashboardResponse> getDashboardData() {
        int totalBreeders = userRepository.countByRole(Role.BREEDER);
        int totalMembers = userRepository.countByRole(Role.MEMBER);
        int totalAuctions = auctionRepository.countByStatus(AuctionStatus.COMPLETED);

        // Tìm người quản lý (Manager) và lấy ví của họ
        User manager = userRepository.findByRole(Role.MANAGER);
        Optional<Wallet> managerWallet = manager != null ? walletRepository.findByUserId(manager.getId()) : null;
        float profit = managerWallet.get().getBalance();

        // Tạo đối tượng response
        DashboardResponse response = new DashboardResponse(totalBreeders, totalMembers, totalAuctions, profit);
        return ApiResponse.<DashboardResponse>builder()
                .code(200)
                .result(response)
                .build();
    }

    @GetMapping("/monthly-auctions")
    public ApiResponse<List<MonthlyAuctionDataResponse>> getMonthlyCompletedAuctionData() {
        int currentYear = LocalDate.now().getYear();
        List<MonthlyAuctionDataResponse> monthlyData = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            // Đếm số lượng đấu giá hoàn thành trong tháng hiện tại
            int auctionCount = auctionRequestRepository.countCompletedAuctionsByEndTimeYearAndMonth(currentYear, month);

            // Thêm dữ liệu vào danh sách
            monthlyData.add(new MonthlyAuctionDataResponse(month, auctionCount));
        }

        return ApiResponse.<List<MonthlyAuctionDataResponse>>builder()
                .code(200)
                .result(monthlyData)
                .build();
    }
}
