package com.linhtn.shared_kernel.application.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationException extends RuntimeException {
    private final String errorCode;
    private final String errorMessage;

    public ApplicationException(String errorCode, String message) {
        this.errorCode = errorCode;
        this.errorMessage = message;
        super(message);
    }
}
