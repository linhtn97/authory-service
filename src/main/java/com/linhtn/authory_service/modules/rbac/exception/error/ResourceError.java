package com.linhtn.authory_service.modules.rbac.exception.error;

import lombok.Getter;

@Getter
public enum ResourceError {
    RESOURCE_NOT_FOUND("error.resource.not_found"),  // 404 - Not Found
    RESOURCE_ALREADY_EXISTS("error.resource.already_exists");  // 409 - Conflict
    ;
    private final String message;

    ResourceError(String message) {
        this.message = message;
    }
}