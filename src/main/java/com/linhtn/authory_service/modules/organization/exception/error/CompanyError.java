package com.linhtn.authory_service.modules.organization.exception.error;

import lombok.Getter;

@Getter
public enum CompanyError {
    COMPANY_NOT_FOUND("error.company.not_found"),  // 404 - Not Found
    COMPANY_ALREADY_EXISTS("error.company.already_exists"),  // 409 - Conflict
    ;
    private final String message;

    CompanyError(String message) {
        this.message = message;
    }
}