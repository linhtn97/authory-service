package com.linhtn.authory_service.modules.partner.exception.unchecked;


import com.linhtn.shared_kernel.common.exception.unchecked.NotFoundException;

public class PartnerNotFoundException extends NotFoundException {
    public PartnerNotFoundException(String errorCode, String message) {
        super(errorCode, message);
    }
}