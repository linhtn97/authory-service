package com.linhtn.authory_service.modules.user.exception.unchecked;

import com.linhtn.shared_kernel.common.exception.unchecked.NotFoundException;

public class CustomerNotFoundException extends NotFoundException {
    public CustomerNotFoundException(String errorCode, String message) {
        super(errorCode, message);
    }
}

