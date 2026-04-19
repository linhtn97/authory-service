package com.linhtn.shared_kernel.common.exception.unchecked;

import lombok.Getter;
import org.springframework.http.HttpStatus;

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

    public ApplicationException(HttpStatus status, String errorCode, String message, Object metadata) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
        this.metadata = metadata;
    }
}
