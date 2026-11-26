package com.linhtn.authory_service.modules.organization.exception.unchecked;

import com.linhtn.authory_service.domain.exception.NotFoundException;

public class DivisionNotFoundException extends NotFoundException {
    public DivisionNotFoundException(String errorCode, String message) {
        super(errorCode, message);
    }
}
