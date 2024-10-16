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
    FISH_NOT_EXISTED(1006, "Fish not existed", HttpStatus.BAD_REQUEST),
    AUCTION_NOT_EXISTED(1007, "AuctionMapper not existed", HttpStatus.NOT_FOUND),
    AUCTION_REQUEST_NOT_EXISTED(1008, "AuctionMapper request not existed", HttpStatus.NOT_FOUND),
    AUCTION_NOT_APPROVE(1009,"AuctionMapper request is not pending approval", HttpStatus.BAD_REQUEST)

    ;

    int code;
    String message;
    HttpStatusCode statusCode;
}
