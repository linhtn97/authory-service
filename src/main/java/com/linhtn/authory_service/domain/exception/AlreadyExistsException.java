package com.linhtn.authory_service.domain.exception;

import com.linhtn.sharedkernel.domain.exception.EntityAlreadyExistsException;

public class AlreadyExistsException extends EntityAlreadyExistsException {
    public AlreadyExistsException(String errorCode, String message) {
        super("Entity", null, message);
    }

    public AlreadyExistsException(String entityName, Object entityId, String message) {
        super(entityName, entityId, message);
    }
}
