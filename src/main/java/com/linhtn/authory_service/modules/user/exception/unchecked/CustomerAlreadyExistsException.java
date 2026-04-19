package com.linhtn.authory_service.modules.user.exception.unchecked;

import com.linhtn.shared_kernel.common.exception.unchecked.AlreadyExistsException;

public class CustomerAlreadyExistsException extends AlreadyExistsException {
    public CustomerAlreadyExistsException(String errorCode, String message) {
        super(errorCode, message);
    }
}
