package com.linhtn.authory_service.modules.auth.exception.unchecked;


import com.linhtn.shared_kernel.common.exception.unchecked.AlreadyExistsException;

public class ApiCredentialAlreadyExistsException extends AlreadyExistsException {
    public ApiCredentialAlreadyExistsException(String errorCode, String message) {
        super(errorCode, message);
    }
}