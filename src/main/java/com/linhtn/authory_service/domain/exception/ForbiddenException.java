package com.linhtn.authory_service.domain.exception;

import com.linhtn.sharedkernel.application.exception.ApplicationException;

public class ForbiddenException extends ApplicationException {
    public ForbiddenException(String errorCode, String message) {
        super(errorCode, message);
    }
}
