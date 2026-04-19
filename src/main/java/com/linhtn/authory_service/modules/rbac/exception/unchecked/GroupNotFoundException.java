package com.linhtn.authory_service.modules.rbac.exception.unchecked;


import com.linhtn.shared_kernel.common.exception.unchecked.NotFoundException;

public class GroupNotFoundException extends NotFoundException {
    public GroupNotFoundException(String errorCode, String message) {
        super(errorCode, message);
    }
}