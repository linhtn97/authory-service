package com.linhtn.shared_kernel.common.exception.unchecked;

import com.linhtn.shared_kernel.common.exception.unchecked.ApplicationException;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends ApplicationException {
    public UnauthorizedException(String errorCode, String message) {
        super(HttpStatus.UNAUTHORIZED, errorCode, message);
    }
}
