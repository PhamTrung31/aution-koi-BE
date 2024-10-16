package swp.auctionkoi.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_EXISTED(1001, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1002, "Username must be ....", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1003, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1004, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1005, "You have no permission", HttpStatus.FORBIDDEN),
    STAFF_EXISTED(1006, "Staff existed", HttpStatus.CONFLICT),
    AUCTION_NOT_EXISTED(1006, "Auction not existed", HttpStatus.NOT_FOUND),
    WALLET_NOT_EXISTED(1007, "Wallet not existed", HttpStatus.NOT_FOUND),
    USER_EXISTED_AUCTION(1008, "User has already joined the auction", HttpStatus.BAD_REQUEST),
    ONLY_MEMBER_ALLOWED(1009, "Only members are allowed to join the auction.", HttpStatus.BAD_REQUEST),
    NOT_ENOUGH_BALANCE(1100, "Not enough balance.", HttpStatus.BAD_REQUEST),
    AUCTION_JOIN_CLOSED(1101, "Auction joined closed", HttpStatus.CONFLICT),
    ADMIN_NOT_FOUND(1102, "Admin not found", HttpStatus.NOT_FOUND),
    DELIVERY_NOT_EXISTED(1103, "Delivery not existed", HttpStatus.NOT_FOUND),
    BREEDER_NOT_FOUND(1104, "Breeder not found", HttpStatus.NOT_FOUND);


    int code;
    String message;
    HttpStatusCode statusCode;
}
