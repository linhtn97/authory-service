package com.linhtn.authory_service.modules.auth.exception.error;

import lombok.Getter;

@Getter
public enum ApiCredentialError {
    API_CREDENTIAL_NOT_FOUND("error.api_credential.not_found"),  // 404 - Not Found
    API_CREDENTIAL_ALREADY_EXISTS("error.api_credential.already_exists"),  // 409 - Conflict
    ;
    private final String message;

    ApiCredentialError(String message) {
        this.message = message;
    }
}