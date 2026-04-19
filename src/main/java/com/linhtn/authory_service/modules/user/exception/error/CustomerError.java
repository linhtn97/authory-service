package com.linhtn.authory_service.modules.user.exception.error;

import lombok.Getter;

@Getter
public enum CustomerError {
    CUSTOMER_NOT_FOUND("error.customer.not_found"),  // 404 - Not Found
    CUSTOMER_ALREADY_EXISTS("error.customer.already_exists"), // 409 - Conflict
    ;
    private final String message;

    CustomerError(String message) {
        this.message = message;
    }
}
