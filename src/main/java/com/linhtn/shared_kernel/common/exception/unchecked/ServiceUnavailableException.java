package com.linhtn.shared_kernel.common.exception.unchecked;

import org.springframework.http.HttpStatus;

public class ServiceUnavailableException extends ApplicationException {
    public ServiceUnavailableException(String errorCode, String message) {
        super(HttpStatus.SERVICE_UNAVAILABLE, errorCode, message);
    }
}
