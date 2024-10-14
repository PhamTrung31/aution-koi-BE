package swp.auctionkoi.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
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
    AUCTION_NOT_FOUND(1006, "Auction not found", HttpStatus.NOT_FOUND),
    NOT_ENOUGH_FUNDS(1007, "Not enough funds", HttpStatus.BAD_REQUEST),
    AUCTION_NOT_STARTED(1008, "Auction not started.", HttpStatus.BAD_REQUEST),
    INVALID_BID_AMOUNT(1009, "Invalid bid amount", HttpStatus.BAD_REQUEST),
    AUCTION_AUTO_BID_EXCEEDS_MAX(1010, "Auction auto bid exceeds maximum", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_FUNDS(1011, "Insufficient funds", HttpStatus.BAD_REQUEST),
    BID_ALREADY_PLACED(1012, "Bid already placed", HttpStatus.BAD_REQUEST),
    INVALID_AUCTION_TYPE(1013, "Invalid Auction type", HttpStatus.BAD_REQUEST),
    MONEY_IN_WALLET_NOT_ENOUGH(1014, "Money in your wallet not enough to continues bid", HttpStatus.BAD_REQUEST),
    LOWER_CURRENT_PRICE(1015, "Your bid price is lower than current price", HttpStatus.BAD_REQUEST),
    WALLET_IS_NOT_EXIST(1016, "Wallet is not exist", HttpStatus.NOT_FOUND),
    AUCTION_REQUEST_NOT_FOUND(1017, "Auction request not found", HttpStatus.NOT_FOUND),
    INVALID_TRANSACTION_ID(1018, "Invalid transaction id", HttpStatus.BAD_REQUEST),
    INVALID_WALLET_ID(1019, "Invalid wallet id", HttpStatus.BAD_REQUEST),
    INVALID_USER_ID(1020, "Invalid user id", HttpStatus.BAD_REQUEST),
    NO_PAYMENT(1021, "Have no payment", HttpStatus.BAD_REQUEST),

    FISH_NOT_EXISTED(1006, "Fish not existed", HttpStatus.BAD_REQUEST),
    AUCTION_NOT_EXISTED(1007, "Auction not existed", HttpStatus.BAD_REQUEST),
    AUCTION_REQUEST_NOT_EXISTED(1008, "Auction request not existed", HttpStatus.BAD_REQUEST)
    ;

    int code;
    String message;
    HttpStatusCode statusCode;

    public void setMessage(String message) {
        this.message = message;
    }
}
