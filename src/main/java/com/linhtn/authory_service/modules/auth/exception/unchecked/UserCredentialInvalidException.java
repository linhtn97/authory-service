package com.linhtn.authory_service.modules.auth.exception.unchecked;

import com.linhtn.authory_service.domain.exception.InvalidInputException;

public class UserCredentialInvalidException extends InvalidInputException {
    public UserCredentialInvalidException(String errorCode, String message) {
        super(errorCode, message);
    }
}
