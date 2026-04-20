package com.linhtn.authory_service.modules.organization.exception.unchecked;

import com.linhtn.shared_kernel.common.exception.unchecked.NotFoundException;

public class CenterNotFoundException extends NotFoundException {
    public CenterNotFoundException(String errorCode, String message) {
        super(errorCode, message);
    }
}
