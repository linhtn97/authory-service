package com.linhtn.authory_service.modules.auth.exception.unchecked;

import com.linhtn.authory_service.domain.exception.AlreadyExistsException;

public class ApiCredentialAlreadyExistsException extends AlreadyExistsException {
    public ApiCredentialAlreadyExistsException(String errorCode, String message) {
        super(errorCode, message);
    }
}
