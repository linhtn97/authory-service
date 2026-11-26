package com.linhtn.authory_service.modules.rbac.exception.unchecked;

import com.linhtn.authory_service.domain.exception.AlreadyExistsException;

public class RoleAlreadyExistsException extends AlreadyExistsException {
    public RoleAlreadyExistsException(String errorCode, String message) {
        super(errorCode, message);
    }
}
