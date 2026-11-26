package com.linhtn.authory_service.modules.organization.exception.unchecked;

import com.linhtn.authory_service.domain.exception.NotFoundException;

public class DepartmentNotFoundException extends NotFoundException {
    public DepartmentNotFoundException(String errorCode, String message) {
        super(errorCode, message);
    }
}
