package com.linhtn.authory_service.domain.exception;

import com.linhtn.sharedkernel.application.exception.ApplicationException;

public class InvalidInputException extends ApplicationException {
    public InvalidInputException(String errorCode, String message) {
        super(errorCode, message);
    }
}
