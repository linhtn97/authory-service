package com.linhtn.authory_service.modules.abac.exception.unchecked;

import com.linhtn.authory_service.domain.exception.AlreadyExistsException;

public class AttributeAlreadyExistsException extends AlreadyExistsException {
    public AttributeAlreadyExistsException(String errorCode, String message) {
        super(errorCode, message);
    }
}
