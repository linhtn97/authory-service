package com.linhtn.authory_service.modules.abac.exception.unchecked;

import com.linhtn.authory_service.domain.exception.NotFoundException;

public class AttributeNotFoundException extends NotFoundException {
    public AttributeNotFoundException(String errorCode, String message) {
        super(errorCode, message);
    }
}
