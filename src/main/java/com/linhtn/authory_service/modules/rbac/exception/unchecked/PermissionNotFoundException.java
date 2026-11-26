package com.linhtn.authory_service.modules.rbac.exception.unchecked;

import com.linhtn.authory_service.domain.exception.NotFoundException;

public class PermissionNotFoundException extends NotFoundException {
    public PermissionNotFoundException(String errorCode, String message) {
        super(errorCode, message);
    }
}
