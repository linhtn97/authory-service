package com.linhtn.authory_service.modules.rbac.exception.unchecked;

import com.linhtn.authory_service.domain.exception.AlreadyExistsException;

public class PermissionAlreadyExistsException extends AlreadyExistsException {
    public PermissionAlreadyExistsException(String errorCode, String message) {
        super(errorCode, message);
    }
}
