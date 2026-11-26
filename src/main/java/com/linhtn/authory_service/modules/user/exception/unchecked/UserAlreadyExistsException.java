package com.linhtn.authory_service.modules.user.exception.unchecked;

import com.linhtn.authory_service.domain.exception.AlreadyExistsException;

public class UserAlreadyExistsException extends AlreadyExistsException {
    public UserAlreadyExistsException(String errorCode, String message) {
        super(errorCode, message);
    }
}
