package swp.auctionkoi.service.transaction.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import swp.auctionkoi.dto.request.transaction.TransactionRequestFilterDTO;
import swp.auctionkoi.models.Transaction;
import swp.auctionkoi.repository.TransactionRepository;
import swp.auctionkoi.service.transaction.MemberTransactionService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MemberTransactionServiceImpl implements MemberTransactionService {

    TransactionRepository transactionRepository;

    @Override
    public List<Transaction> getAllTransactionsByUserId(int userId) {
        return transactionRepository.findListTransactionByUserId(userId);
    }

    public List<Transaction> getFilteredAndSortedTransactions(int userId, TransactionRequestFilterDTO filterDTO) {
        // Lấy tất cả giao dịch của user
        List<Transaction> transactions = transactionRepository.findListTransactionByUserId(userId);

        // Kiểm tra nếu tất cả các trường đều là null, trả về toàn bộ giao dịch
        if (filterDTO.getFromDate() == null && filterDTO.getToDate() == null && filterDTO.getTransactionType() == null) {
            return transactions; // Trả về toàn bộ danh sách giao dịch
        }

        // Lọc các giao dịch theo từ ngày, đến ngày và loại giao dịch
        List<Transaction> filteredTransactions = transactions.stream()
                .filter(transaction -> filterByDate(transaction, filterDTO))
                .filter(transaction -> filterByTransactionType(transaction, filterDTO))
                .collect(Collectors.toList());

        // Sắp xếp giao dịch theo transactionDate (có thể là giảm dần hoặc tăng dần)
        return filteredTransactions.stream()
                .sorted((t1, t2) -> t1.getTransactionDate().compareTo(t2.getTransactionDate()))
                .collect(Collectors.toList());
    }

    // Kiểm tra xem giao dịch có nằm trong khoảng ngày không
    private boolean filterByDate(Transaction transaction, TransactionRequestFilterDTO filterDTO) {
        ZonedDateTime zonedDateTime = transaction.getTransactionDate().atZone(ZoneId.systemDefault());
        LocalDate transactionDate = zonedDateTime.toLocalDate();
        boolean isAfterFromDate = filterDTO.getFromDate() == null || !transactionDate.isBefore(filterDTO.getFromDate());
        boolean isBeforeToDate = filterDTO.getToDate() == null || !transactionDate.isAfter(filterDTO.getToDate());
        return isAfterFromDate && isBeforeToDate;
    }

    // Kiểm tra xem giao dịch có thuộc loại giao dịch được yêu cầu không
    private boolean filterByTransactionType(Transaction transaction, TransactionRequestFilterDTO filterDTO) {
        return filterDTO.getTransactionType() == null || transaction.getTransactionType().equals(filterDTO.getTransactionType());
    }
}
