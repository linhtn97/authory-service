package com.linhtn.authory_service.modules.user.exception.unchecked;

import com.linhtn.shared_kernel.common.exception.unchecked.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String errorCode, String message) {
        super(errorCode, message);
    }
}
