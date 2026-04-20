package com.linhtn.authory_service.modules.organization.exception.error;

import lombok.Getter;

@Getter
public enum DepartmentError {
    DEPARTMENT_NOT_FOUND("error.department.not_found"), // 404 - Not Found
    DEPARTMENT_ALREADY_EXISTS("error.department.already_exists"); // 409 - Conflict
    ;
    private final String message;

    DepartmentError(String message) {
        this.message = message;
    }
}
