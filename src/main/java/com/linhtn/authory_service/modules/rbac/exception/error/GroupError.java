package com.linhtn.authory_service.modules.rbac.exception.error;

import lombok.Getter;

@Getter
public enum GroupError {
    GROUP_NOT_FOUND("error.group.not_found"),  // 404 - Not Found
    GROUP_ALREADY_EXISTS("error.group.already_exists");  // 409 - Conflict
    ;
    private final String message;

    GroupError(String message) {
        this.message = message;
    }
}