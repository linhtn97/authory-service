package com.linhtn.authory_service.modules.auth.exception.unchecked;

import com.linhtn.authory_service.domain.exception.AlreadyExistsException;

public class UserCredentialAlreadyExistsException extends AlreadyExistsException {
    public UserCredentialAlreadyExistsException(String errorCode, String message) {
        super(errorCode, message);
    }
}
