package com.linhtn.shared_kernel.common.exception.unchecked;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;
    private final String message;
    private Object metadata;

    public ApplicationException(HttpStatus status, String errorCode, String message) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
    }

    public ApplicationException(
            HttpStatus status, String errorCode, String message, Object metadata) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
        this.metadata = metadata;
    }
}
