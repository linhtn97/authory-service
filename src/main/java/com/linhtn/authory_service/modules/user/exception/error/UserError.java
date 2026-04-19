package com.linhtn.authory_service.modules.user.exception.error;


import lombok.Getter;

@Getter
public enum UserError {
    USER_NOT_FOUND("error.user.not_found"),  // 404 - Not Found
    USER_ALREADY_EXISTS("error.user.already_exists"), // 409 - Conflict
    ;
    private final String message;

    UserError(String message) {
        this.message = message;
    }
}
