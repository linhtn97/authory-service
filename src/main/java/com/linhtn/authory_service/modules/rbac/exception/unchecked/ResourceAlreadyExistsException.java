package com.linhtn.authory_service.modules.rbac.exception.unchecked;

import com.linhtn.authory_service.domain.exception.AlreadyExistsException;

public class ResourceAlreadyExistsException extends AlreadyExistsException {
    public ResourceAlreadyExistsException(String errorCode, String message) {
        super(errorCode, message);
    }
}
