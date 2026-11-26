package com.linhtn.authory_service.modules.auth.exception.error;

import lombok.Getter;

@Getter
public enum UserCredentialError {
    USER_CREDENTIAL_NOT_FOUND("error.user_credential.not_found"), // 404 - Not Found
    USER_CREDENTIAL_ALREADY_EXISTS("error.user_credential.already_exists"), // 409 - Conflict
    USER_CREDENTIAL_INVALID("error.user_credential.invalid"),
    USER_ACCOUNT_LOCKED("error.user_credential.account_locked"),
    MFA_CODE_INVALID("error.user_credential.mfa_invalid"),
    PASSWORD_RESET_TOKEN_INVALID("error.user_credential.password_reset_token_invalid"),
    ;
    private final String message;

    UserCredentialError(String message) {
        this.message = message;
    }
}
