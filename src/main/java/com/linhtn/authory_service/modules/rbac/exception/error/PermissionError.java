package com.linhtn.authory_service.modules.rbac.exception.error;

import lombok.Getter;

@Getter
public enum PermissionError {
    PERMISSION_NOT_FOUND("error.permission.not_found"), // 404 - Not Found
    PERMISSION_ALREADY_EXISTS("error.permission.already_exists"); // 409 - Conflict
    ;
    private final String message;

    PermissionError(String message) {
        this.message = message;
    }
}
