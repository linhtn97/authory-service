package com.linhtn.authory_service.modules.auth.exception.error;

import lombok.Getter;

@Getter
public enum TokenError {
    TOKEN_NOT_FOUND("error.token.not_found"), // 404 - Not Found
    TOKEN_ALREADY_EXISTS("error.token.already_exists"), // 409 - Conflict
    ;
    private final String message;

    TokenError(String message) {
        this.message = message;
    }
}
