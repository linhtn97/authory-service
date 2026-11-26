package com.linhtn.authory_service.modules.rbac.exception.unchecked;

import com.linhtn.authory_service.domain.exception.NotFoundException;

public class RoleNotFoundException extends NotFoundException {
    public RoleNotFoundException(String errorCode, String message) {
        super(errorCode, message);
    }
}
