package com.linhtn.authory_service.modules.abac.exception.error;

import lombok.Getter;

@Getter
public enum AttributeError {
    ATTRIBUTE_NOT_FOUND("error.attribute.not_found"),  // 404 - Not Found
    ATTRIBUTE_ALREADY_EXISTS("error.attribute.already_exists");  // 409 - Conflict
    private final String message;

    AttributeError(String message) {
        this.message = message;
    }
}
