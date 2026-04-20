package com.linhtn.authory_service.modules.organization.exception.error;

import lombok.Getter;

@Getter
public enum PositionError {
    POSITION_NOT_FOUND("error.position.not_found"), // 404 - Not Found
    POSITION_ALREADY_EXISTS("error.position.already_exists"); // 409 - Conflict
    ;
    private final String message;

    PositionError(String message) {
        this.message = message;
    }
}
