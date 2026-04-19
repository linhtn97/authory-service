package com.linhtn.authory_service.modules.abac.exception.unchecked;


import com.linhtn.shared_kernel.common.exception.unchecked.AlreadyExistsException;

public class UserAttributeAlreadyExistsException extends AlreadyExistsException {
    public UserAttributeAlreadyExistsException(String errorCode, String message) {
        super(errorCode, message);
    }
}