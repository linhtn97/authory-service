package com.linhtn.authory_service.modules.rbac.exception.error;


import lombok.Getter;

@Getter
public enum RoleError {
    ROLE_NOT_FOUND("error.role.not_found"),  // 404 - Not Found
    ROLE_ALREADY_EXISTS("error.role.already_exists");  // 409 - Conflict
    ;
    private final String message;

    RoleError(String message) {
        this.message = message;
    }
}