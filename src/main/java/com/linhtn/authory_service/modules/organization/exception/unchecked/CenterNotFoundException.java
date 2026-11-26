package com.linhtn.authory_service.modules.organization.exception.unchecked;

import com.linhtn.authory_service.domain.exception.NotFoundException;

public class CenterNotFoundException extends NotFoundException {
    public CenterNotFoundException(String errorCode, String message) {
        super(errorCode, message);
    }
}
