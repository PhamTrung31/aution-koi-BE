package swp.auctionkoi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error"),
    USER_EXISTED(1001, "User existed"),
    USERNAME_INVALID(1002, "Username must be ...."),
    USER_NOT_EXISTED(1003, "User not existed"),
    UNAUTHENTICATED(1004, "Unauthenticated")
    ;

    private int code;
    private String message;

}
