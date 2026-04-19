package com.linhtn.shared_kernel.domain.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DomainException extends RuntimeException {
    private final String errorCode;
    private final String errorMessage;

    public DomainException(String errorCode, String message) {
        this.errorCode = errorCode;
        this.errorMessage = message;
        super(message);
    }
}
