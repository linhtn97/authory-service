package com.linhtn.shared_kernel.common.exception.unchecked;

import com.linhtn.shared_kernel.common.exception.unchecked.ApplicationException;
import org.springframework.http.HttpStatus;

public class AlreadyExistsException extends ApplicationException {
    public AlreadyExistsException(String errorCode, String message) {
        super(HttpStatus.CONFLICT, errorCode, message);
    }
}
