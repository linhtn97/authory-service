package com.linhtn.shared_kernel.common.exception.unchecked;

import com.linhtn.shared_kernel.common.exception.unchecked.ApplicationException;
import org.springframework.http.HttpStatus;

public class NotFoundException extends ApplicationException {
    public NotFoundException(String errorCode, String message) {
        super(HttpStatus.NOT_FOUND, errorCode, message);
    }
}