package com.julytus.EBook.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

    USER_EXISTED(400, "User existed", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(400, "User not existed", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(401, "Unauthorized", HttpStatus.UNAUTHORIZED),
    ACCESS_DINED(403, "Access denied", HttpStatus.FORBIDDEN),
    TOKEN_INVALID(400, "Token invalid", HttpStatus.BAD_REQUEST),
    BOOK_NOT_FOUND(404, "Book not found", HttpStatus.NOT_FOUND),
    ROLE_EXISTED(400, "PredefinedRole existed", HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXISTED(400, "PredefinedRole not existed", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_EXPIRED(401, "Refresh token expired", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_INVALID(401, "Refresh token invalid", HttpStatus.BAD_REQUEST),
    TOKEN_BLACK_LIST(400, "Token black list", HttpStatus.BAD_REQUEST),
    SIGN_OUT_FAILED(400, "Sign out failed", HttpStatus.BAD_REQUEST),
    JWT_EXPIRED(401, "JWT token has expired", HttpStatus.UNAUTHORIZED),
    JWT_MALFORMED(400, "JWT token is malformed", HttpStatus.BAD_REQUEST),
    JWT_SIGNATURE_INVALID(401, "JWT signature is invalid", HttpStatus.UNAUTHORIZED),
    JWT_UNSUPPORTED(400, "JWT token is unsupported", HttpStatus.BAD_REQUEST),
    JWT_CLAIMS_EMPTY(400, "JWT claims string is empty", HttpStatus.BAD_REQUEST),
    ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
