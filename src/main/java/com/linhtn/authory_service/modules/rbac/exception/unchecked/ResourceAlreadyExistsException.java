package com.linhtn.authory_service.modules.rbac.exception.unchecked;

import com.linhtn.shared_kernel.common.exception.unchecked.AlreadyExistsException;

public class ResourceAlreadyExistsException extends AlreadyExistsException {
    public ResourceAlreadyExistsException(String errorCode, String message) {
        super(errorCode, message);
    }
}
