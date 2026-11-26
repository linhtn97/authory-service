package com.linhtn.authory_service.modules.auth.exception.unchecked;

import com.linhtn.authory_service.domain.exception.NotFoundException;

public class ApiCredentialNotFoundException extends NotFoundException {
    public ApiCredentialNotFoundException(String errorCode, String message) {
        super(errorCode, message);
    }
}
