package com.linhtn.authory_service.modules.user.exception.error;

import lombok.Getter;

@Getter
public enum EmployeeError {
    EMPLOYEE_NOT_FOUND("error.employee.not_found"), // 404 - Not Found
    EMPLOYEE_ALREADY_EXISTS("error.employee.already_exists"), // 409 - Conflict
    ;
    private final String message;

    EmployeeError(String message) {
        this.message = message;
    }
}
