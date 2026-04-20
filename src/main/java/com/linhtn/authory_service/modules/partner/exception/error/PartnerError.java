package com.linhtn.authory_service.modules.partner.exception.error;

import lombok.Getter;

@Getter
public enum PartnerError {
    PARTNER_NOT_FOUND("error.partner.not_found"), // 404 - Not Found
    PARTNER_ALREADY_EXISTS("error.partner.already_exists"); // 409 - Conflict
    ;
    private final String message;

    PartnerError(String message) {
        this.message = message;
    }
}
