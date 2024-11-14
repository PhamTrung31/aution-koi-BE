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
    INVALID_KEY(1000, "Uncategorized error", HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error!", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_EXISTED(1001, "User existed!", HttpStatus.BAD_REQUEST),
    //    USERNAME_INVALID(1002, "Username must be ....", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1003, "User not existed!", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1004, "Unauthenticated!", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1005, "You have no permission!", HttpStatus.FORBIDDEN),
    STAFF_EXISTED(1006, "Staff existed!", HttpStatus.CONFLICT),
    AUCTION_NOT_EXISTED(1007, "Auction not existed!", HttpStatus.NOT_FOUND),
    WALLET_NOT_EXISTED(1008, "Wallet not existed!", HttpStatus.NOT_FOUND),
    USER_EXISTED_AUCTION(1009, "User has already joined the auction!", HttpStatus.BAD_REQUEST),
    ONLY_MEMBER_ALLOWED(1010, "Only members are allowed to join the auction!", HttpStatus.BAD_REQUEST),
    NOT_ENOUGH_BALANCE(1011, "Not enough balance!", HttpStatus.BAD_REQUEST),
    AUCTION_JOIN_CLOSED(1012, "Auction joined closed!", HttpStatus.CONFLICT),
    ADMIN_NOT_FOUND(1013, "Admin not found!", HttpStatus.NOT_FOUND),
    DELIVERY_NOT_EXISTED(1014, "Delivery not existed!", HttpStatus.NOT_FOUND),
    AUCTION_NOT_FOUND(1016, "Auction not found!", HttpStatus.NOT_FOUND),
    NOT_ENOUGH_FUNDS(1017, "Not enough fund!s", HttpStatus.BAD_REQUEST),
    AUCTION_NOT_STARTED(1018, "Auction not started!", HttpStatus.BAD_REQUEST),
    INVALID_BID_AMOUNT(1019, "Invalid bid amoun!t", HttpStatus.BAD_REQUEST),
    AUCTION_AUTO_BID_EXCEEDS_MAX(1020, "Auction auto bid exceeds maximum!", HttpStatus.BAD_REQUEST),
    BID_ALREADY_PLACED(1022, "Bid already placed!", HttpStatus.BAD_REQUEST),
    INVALID_AUCTION_TYPE(1023, "Invalid Auction type!", HttpStatus.BAD_REQUEST),
    MONEY_IN_WALLET_NOT_ENOUGH(1024, "Money in your wallet not enough to continues bid!", HttpStatus.BAD_REQUEST),
    LOWER_CURRENT_PRICE(1025, "Your bid price must bigger than current highest!", HttpStatus.BAD_REQUEST),
    AUCTION_REQUEST_NOT_FOUND(1027, "Auction request not found!", HttpStatus.NOT_FOUND),
    INVALID_TRANSACTION_ID(1028, "Invalid transaction id!", HttpStatus.BAD_REQUEST),
    INVALID_USER_ID(1030, "Invalid user id!", HttpStatus.BAD_REQUEST),
    NO_PAYMENT(1031, "Have no payment!", HttpStatus.BAD_REQUEST),

    FISH_NOT_EXISTED(1032, "Fish not existed", HttpStatus.BAD_REQUEST),
    AUCTION_REQUEST_NOT_EXISTED(1033, "Auction request not existed!", HttpStatus.BAD_REQUEST),

    NOT_FOUND_BID_HIGHEST(1034, "Can not found highest bid!", HttpStatus.NOT_FOUND),
    WRONG_PASSWORD(1035, "Wrong password!", HttpStatus.BAD_REQUEST),
    FISH_NOT_AVAILABLE(1036, "Fish not available for create an auction!", HttpStatus.BAD_REQUEST),
    INVALID_PRICE(1037, "Invalid price!", HttpStatus.BAD_REQUEST),
    INVALID_AUCTION_TIME(1038, "Start time is after end time!", HttpStatus.BAD_REQUEST),
    START_TIME_TOO_CLOSED(1039, "Start time is too closed, start time must be at least one day from current time!", HttpStatus.BAD_REQUEST),
    CANT_CANCEL_REQUEST(1040, "Your request or fish is invalid!", HttpStatus.BAD_REQUEST),
    INVALID_AUCTION_REQUEST_AND_FISH_STATE(1041, "Your request and fish have invalid state!", HttpStatus.BAD_REQUEST),
    USER_NOT_IN_AUCTION(1042, "You not in auction", HttpStatus.NOT_FOUND),
    INVALID_FISH_STATE(1043, "Fish's state is invalid", HttpStatus.BAD_REQUEST),
    INVALID_AUCTION_REQUEST_STATE(1044, "Auction's state is invalid", HttpStatus.BAD_REQUEST),
    NOT_BELONG_TO_BREEDER(1045, "Fish not belong to breeder!", HttpStatus.BAD_REQUEST),
    ERROR_UPDATE(1046, "Update failed!", HttpStatus.BAD_REQUEST),
    STAFF_NOT_FOUND(1047, "Staff not existed", HttpStatus.NOT_FOUND),
    INCORRECT_PASSWORD(1048, "Incorrect password", HttpStatus.BAD_REQUEST),
    STAFF_ID_REQUIRED(1049, "Required staff to assign",HttpStatus.NOT_FOUND ),
    AlREADY_WIN(1050, "You are the winner!", HttpStatus.BAD_REQUEST),

    INVALID_AUCTION_DURATION(1051,"Auction duration must be at least 10 minutes",HttpStatus.BAD_REQUEST),
    DUPLICATE_FISH_NAME(1052,"Duplicated fish name",HttpStatus.BAD_REQUEST),
    INVALID_PAYMENT_STATUS(1053,"Invalid payment status",HttpStatus.BAD_REQUEST),
    NOT_FOLLOW_INCREMENT_STEP(1054, "Must follow the increment step!", HttpStatus.BAD_REQUEST),
    AUCTION_TOO_CLOSE_TO_PREVIOUS(1055,"End time of previous auction is closed than start time", HttpStatus.BAD_REQUEST),
    CANT_NOT_BID(1056,"Too close end time!", HttpStatus.BAD_REQUEST),
    ACCOUNT_DISABLED(1057," Your account is disabled!", HttpStatus.BAD_REQUEST),
    ;
    //    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
//    USER_EXISTED(1001, "User existed", HttpStatus.BAD_REQUEST),
//    USERNAME_INVALID(1002, "Username must be ....", HttpStatus.BAD_REQUEST),
//    USER_NOT_EXISTED(1003, "User not existed", HttpStatus.NOT_FOUND),
//    UNAUTHENTICATED(1004, "Unauthenticated", HttpStatus.UNAUTHORIZED),
//    UNAUTHORIZED(1005, "You have no permission", HttpStatus.FORBIDDEN),
//    STAFF_EXISTED(1006, "Staff existed", HttpStatus.CONFLICT),
//    AUCTION_NOT_EXISTED(1006, "Auction not existed", HttpStatus.NOT_FOUND),
//    WALLET_NOT_EXISTED(1007, "Wallet not existed", HttpStatus.NOT_FOUND),
//    USER_EXISTED_AUCTION(1008, "User has already joined the auction", HttpStatus.BAD_REQUEST),
//    ONLY_MEMBER_ALLOWED(1009, "Only members are allowed to join the auction.", HttpStatus.BAD_REQUEST),
//    NOT_ENOUGH_BALANCE(1100, "Not enough balance.", HttpStatus.BAD_REQUEST),
//    AUCTION_JOIN_CLOSED(1101, "Auction joined closed", HttpStatus.CONFLICT),
//    ADMIN_NOT_FOUND(1102, "Admin not found", HttpStatus.NOT_FOUND),
//    DELIVERY_NOT_EXISTED(1103, "Delivery not existed", HttpStatus.NOT_FOUND),
//    BREEDER_NOT_FOUND(1104, "Breeder not found", HttpStatus.NOT_FOUND),
//    INVALID_AMOUNT(1105,"Invalid amount", HttpStatus.BAD_REQUEST),
//    INVALID_PAYMENT(1106,"Payment request not found or already processed",HttpStatus.BAD_REQUEST),
//    INCORRECT_PASSWORD(1107,"Incorrect password",HttpStatus.BAD_REQUEST),
//    STAFF_NOT_FOUND(1108,"Staff not existed",HttpStatus.NOT_FOUND);
    int code;
    String message;
    HttpStatusCode statusCode;
    }
