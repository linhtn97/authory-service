package com.linhtn.authory_service.modules.auth.exception.unchecked;

import java.time.LocalDateTime;

import com.linhtn.authory_service.domain.exception.InvalidInputException;

import lombok.Getter;

@Getter
public class UserAccountLockedException extends InvalidInputException {
    private final LocalDateTime lockedUntil;

    public UserAccountLockedException(String errorCode, String message, LocalDateTime lockedUntil) {
        super(errorCode, message);
        this.lockedUntil = lockedUntil;
    }
}
