package com.linhtn.authory_service.modules.auth.exception.unchecked;

import com.linhtn.shared_kernel.common.exception.unchecked.AlreadyExistsException;

public class TokenAlreadyExistsException extends AlreadyExistsException {
    public TokenAlreadyExistsException(String errorCode, String message) {
        super(errorCode, message);
    }
}
