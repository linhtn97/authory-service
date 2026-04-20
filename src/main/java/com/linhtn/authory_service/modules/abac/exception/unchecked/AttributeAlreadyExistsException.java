package com.linhtn.authory_service.modules.abac.exception.unchecked;

import com.linhtn.shared_kernel.common.exception.unchecked.AlreadyExistsException;

public class AttributeAlreadyExistsException extends AlreadyExistsException {
    public AttributeAlreadyExistsException(String errorCode, String message) {
        super(errorCode, message);
    }
}
