package com.linhtn.authory_service.modules.abac.exception.unchecked;

import com.linhtn.authory_service.domain.exception.NotFoundException;

public class UserAttributeNotFoundException extends NotFoundException {
    public UserAttributeNotFoundException(String errorCode, String message) {
        super(errorCode, message);
    }
}
