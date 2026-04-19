package com.linhtn.authory_service.modules.organization.exception.error;

import lombok.Getter;

@Getter
public enum DivisionError {
    DIVISION_NOT_FOUND("error.division.not_found"),  // 404 - Not Found
    DIVISION_ALREADY_EXISTS("error.division.already_exists");  // 409 - Conflict
    private final String message;

    DivisionError(String message) {
        this.message = message;
    }
}
