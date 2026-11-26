package com.linhtn.authory_service.modules.rbac.exception.unchecked;

import com.linhtn.authory_service.domain.exception.AlreadyExistsException;

public class GroupAlreadyExistsException extends AlreadyExistsException {
    public GroupAlreadyExistsException(String errorCode, String message) {
        super(errorCode, message);
    }
}
