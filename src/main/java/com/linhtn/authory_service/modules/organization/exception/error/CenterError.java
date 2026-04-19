package com.linhtn.authory_service.modules.organization.exception.error;

import lombok.Getter;

@Getter
public enum CenterError {
    CENTER_NOT_FOUND("error.center.not_found"),  // 404 - Not Found
    CENTER_ALREADY_EXISTS("error.center.already_exists");  // 409 - Conflict
    private final String message;

    CenterError(String message) {
        this.message = message;
    }
}
