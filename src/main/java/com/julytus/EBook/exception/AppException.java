package com.julytus.EBook.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    private final ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public AppException(String message, Throwable cause, ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
