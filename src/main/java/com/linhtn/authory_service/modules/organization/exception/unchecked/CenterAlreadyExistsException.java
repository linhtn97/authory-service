package com.linhtn.authory_service.modules.organization.exception.unchecked;

import com.linhtn.shared_kernel.common.exception.unchecked.AlreadyExistsException;

public class CenterAlreadyExistsException extends AlreadyExistsException {
    public CenterAlreadyExistsException(String errorCode, String message) {
        super(errorCode, message);
    }
}
