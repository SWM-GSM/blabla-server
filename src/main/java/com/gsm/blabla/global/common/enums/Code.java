package com.gsm.blabla.global.common.enums;

import com.gsm.blabla.global.common.GeneralException;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum Code {
    /*
     * 0번대 - 정상
     * */
    OK(0, HttpStatus.OK, "Ok"),

    /*
     * 2000번대 - Request 관련 오류
     * */
    BAD_REQUEST(2000, HttpStatus.BAD_REQUEST, "Bad request"),
    VALIDATION_ERROR(2001, HttpStatus.BAD_REQUEST, "Validation error"),
    DUPLICATED_NICKNAME(2002, HttpStatus.BAD_REQUEST, "Duplicate nickname"),

    /*
    * 4000번대 - 서버 관련 오류
    * */
    INTERNAL_ERROR(4000, HttpStatus.INTERNAL_SERVER_ERROR, "Internal error"),
    GOOGLE_SERVER_ERROR(4001, HttpStatus.INTERNAL_SERVER_ERROR , "Google server error"),

    /*
     * 5000번대 - 인증 관련 오류
     * */
    UNAUTHORIZED(5000, HttpStatus.UNAUTHORIZED, "User unauthorized"),
    NOT_REGISTERED(5001, HttpStatus.OK, "Need registration"),
    ALREADY_REGISTERED(5002, HttpStatus.BAD_REQUEST, "You're already registered"),
    INVALID_REFRESH_TOKEN(5003, HttpStatus.UNAUTHORIZED, "Invalid refresh token. Sign in again"),
    REFRESH_TOKEN_NOT_FOUND(5004, HttpStatus.UNAUTHORIZED, "Refresh token not found. Sign in again"),
    MALFORMED_JWT(5005, HttpStatus.UNAUTHORIZED, "Malformed jwt format"),
    EXPIRED_JWT(5006, HttpStatus.UNAUTHORIZED, "Jwt expired. Reissue it"),
    UNSUPPORTED_JWT(5007, HttpStatus.UNAUTHORIZED, "Unsupported jwt format"),
    ILLEGAL_JWT(5008, HttpStatus.UNAUTHORIZED, "Illegal jwt format"),
    FORBIDDEN(5009, HttpStatus.FORBIDDEN, "Forbidden"),
    ;

    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;

    public String getMessage(Throwable e) {
        return this.getMessage(this.getMessage() + " - " + e.getMessage());
    }

    public String getMessage(String message) {
        return Optional.ofNullable(message)
            .filter(Predicate.not(String::isBlank))
            .orElse(this.getMessage());
    }

    public static Code valueOf(HttpStatus httpStatus) {
        if (httpStatus == null) {
            throw new GeneralException("HttpStatus is null.");
        }

        return Arrays.stream(values())
            .filter(errorCode -> errorCode.getHttpStatus() == httpStatus)
            .findFirst()
            .orElseGet(() -> {
                if (httpStatus.is4xxClientError()) {
                    return Code.BAD_REQUEST;
                } else if (httpStatus.is5xxServerError()) {
                    return Code.INTERNAL_ERROR;
                } else {
                    return Code.OK;
                }
            });
    }

    @Override
    public String toString() {
        return String.format("%s (%d)", this.name(), this.getCode());
    }
}
