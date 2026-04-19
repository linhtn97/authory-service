package com.linhtn.authory_service.modules.auth.exception.unchecked;

import com.linhtn.shared_kernel.common.exception.unchecked.NotFoundException;

public class UserCredentialNotFoundException extends NotFoundException {
    public UserCredentialNotFoundException(String errorCode, String message) {
        super(errorCode, message);
    }
}
