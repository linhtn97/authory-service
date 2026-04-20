package com.linhtn.authory_service.modules.abac.exception.error;

import lombok.Getter;

@Getter
public enum UserAttributeError {
    USER_ATTRIBUTE_NOT_FOUND("error.user_attribute.not_found"), // 404 - Not Found
    USER_ATTRIBUTE_ALREADY_EXISTS("error.user_attribute.already_exists"); // 409 - Conflict
    private final String message;

    UserAttributeError(String message) {
        this.message = message;
    }
}
