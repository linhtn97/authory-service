package com.linhtn.authory_service.modules.auth.exception.unchecked;

import com.linhtn.authory_service.domain.exception.NotFoundException;

public class TokenNotFoundException extends NotFoundException {
    public TokenNotFoundException(String errorCode, String message) {
        super(errorCode, message);
    }
}
