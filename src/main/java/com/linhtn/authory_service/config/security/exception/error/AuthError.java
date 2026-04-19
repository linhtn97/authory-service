package com.linhtn.authory_service.config.security.exception.error;

import lombok.Getter;

@Getter
public enum AuthError {
    UNAUTHORIZED("error.auth.unauthorized"),
    ACCESS_DENIED("error.auth.access_denied"),
    ;

    private final String message;

    AuthError(String message) {
        this.message = message;
    }
}
