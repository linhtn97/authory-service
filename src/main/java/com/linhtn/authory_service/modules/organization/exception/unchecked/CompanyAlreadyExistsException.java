package com.linhtn.authory_service.modules.organization.exception.unchecked;

import com.linhtn.authory_service.domain.exception.AlreadyExistsException;

public class CompanyAlreadyExistsException extends AlreadyExistsException {
    public CompanyAlreadyExistsException(String errorCode, String message) {
        super(errorCode, message);
    }
}
