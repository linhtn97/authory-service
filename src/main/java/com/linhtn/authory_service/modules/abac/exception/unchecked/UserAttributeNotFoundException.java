package com.linhtn.authory_service.modules.abac.exception.unchecked;


import com.linhtn.shared_kernel.common.exception.unchecked.NotFoundException;

public class UserAttributeNotFoundException extends NotFoundException {
    public UserAttributeNotFoundException(String errorCode, String message) {
        super(errorCode, message);
    }
}
