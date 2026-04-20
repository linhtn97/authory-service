package com.linhtn.authory_service.modules.organization.exception.error;

import lombok.Getter;

@Getter
public enum BranchError {
    BRANCH_NOT_FOUND("error.branch.not_found"), // 404 - Not Found
    BRANCH_ALREADY_EXISTS("error.branch.already_exists"); // 409 - Conflict
    private final String message;

    BranchError(String message) {
        this.message = message;
    }
}
