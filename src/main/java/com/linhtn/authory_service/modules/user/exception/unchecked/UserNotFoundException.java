package com.linhtn.authory_service.modules.user.exception.unchecked;

import com.linhtn.authory_service.domain.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String errorCode, String message) {
        super(errorCode, message);
    }
}
