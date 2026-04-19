package com.linhtn.authory_service.modules.rbac.exception.unchecked;


import com.linhtn.shared_kernel.common.exception.unchecked.AlreadyExistsException;

public class GroupAlreadyExistsException extends AlreadyExistsException {
    public GroupAlreadyExistsException(String errorCode, String message) {
        super(errorCode, message);
    }
}
