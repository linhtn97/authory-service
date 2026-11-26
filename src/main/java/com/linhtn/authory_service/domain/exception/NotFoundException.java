package com.linhtn.authory_service.domain.exception;

import com.linhtn.sharedkernel.domain.exception.EntityNotFoundException;

public class NotFoundException extends EntityNotFoundException {
    public NotFoundException(String errorCode, String message) {
        super("Entity", null, message);
    }

    public NotFoundException(String entityName, Object entityId, String message) {
        super(entityName, entityId, message);
    }
}
