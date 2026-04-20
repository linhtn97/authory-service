package com.linhtn.shared_kernel.common.exception.unchecked;

import org.springframework.http.HttpStatus;

public class InvalidInputException extends ApplicationException {
    public InvalidInputException(String errorCode, String message) {
        super(HttpStatus.BAD_REQUEST, errorCode, message);
    }

    public InvalidInputException(String errorCode, String message, Object details) {
        super(HttpStatus.BAD_REQUEST, errorCode, message, details);
    }
}
