package com.linhtn.authory_service.modules.partner.exception.unchecked;

import com.linhtn.authory_service.domain.exception.AlreadyExistsException;

public class PartnerAlreadyExistsException extends AlreadyExistsException {
    public PartnerAlreadyExistsException(String errorCode, String message) {
        super(errorCode, message);
    }
}
