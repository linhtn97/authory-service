package com.linhtn.authory_service.representation.rest.handler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import com.linhtn.sharedkernel.application.exception.ApplicationException;
import com.linhtn.sharedkernel.domain.exception.DomainException;
import com.linhtn.sharedkernel.representation.ApiErrorResponse;
import com.linhtn.web.i18n.MessageResolver;

@ControllerAdvice
public class GlobalApiExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalApiExceptionHandler.class);

    private final MessageResolver messageResolver;

    public GlobalApiExceptionHandler(MessageResolver messageResolver) {
        this.messageResolver = messageResolver;
    }

    // Handle Custom DomainException
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiErrorResponse> handleDomainException(
            DomainException ex, WebRequest request) {
        logger.error(
                "DomainException: Error Code: {} Message: {}, Path: {}",
                ex.getErrorCode(),
                ex.getMessage(),
                request.getDescription(false),
                ex);

        ApiErrorResponse response = new ApiErrorResponse();
        response.setErrorCode(ex.getErrorCode());
        response.setErrorMessage(messageResolver.getMessage(ex.getErrorMessage()));
        response.setTimestamp(LocalDateTime.now());
        response.setPath(request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Handle Custom ApplicationException
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiErrorResponse> handleApplicationException(
            ApplicationException ex, WebRequest request) {
        logger.error(
                "ApplicationException: Error Code: {} Message: {}, Path: {}",
                ex.getErrorCode(),
                ex.getMessage(),
                request.getDescription(false),
                ex);

        ApiErrorResponse response = new ApiErrorResponse();
        response.setErrorCode(ex.getErrorCode());
        response.setErrorMessage(messageResolver.getMessage(ex.getErrorMessage()));

        response.setTimestamp(LocalDateTime.now());
        response.setPath(request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Handle Validation Errors (DTOs, @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {

        Map<String, String> fieldErrors = new LinkedHashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            String translatedMessage = messageResolver.getMessage(fieldError.getDefaultMessage());

            fieldErrors.merge(
                    fieldError.getField(),
                    translatedMessage,
                    (existing, replacement) -> existing + "; " + replacement);
        }

        String path = request.getDescription(false).replace("uri=", "");
        logger.error("ValidationException: Path: {}, Errors: {}", path, fieldErrors, ex);

        ApiErrorResponse response = new ApiErrorResponse();
        response.setErrorCode("METHOD_ARGUMENT_NOT_VALID");
        response.setErrorMessage(messageResolver.getMessage("error.validation.validation_failed"));
        response.setErrorDetails(fieldErrors);
        response.setTimestamp(LocalDateTime.now());
        response.setPath(path);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Handle Multiple Missing Path Variables
    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ApiErrorResponse> handleMultipleMissingPathVariableExceptions(
            MissingPathVariableException ex, WebRequest request) {

        Map<String, String> missingVariables =
                Map.of(
                        ex.getVariableName(),
                        messageResolver.getMessage(
                                "error.validation.missing.path_variable", ex.getVariableName()));

        logger.error(
                "MissingPathVariableException: Variables: {}, Path: {}",
                missingVariables,
                request.getDescription(false),
                ex);

        ApiErrorResponse response = new ApiErrorResponse();
        response.setErrorCode("MISSING_PATH_VARIABLE");
        response.setErrorMessage(
                messageResolver.getMessage("error.validation.missing.path_variables"));
        response.setErrorDetails(missingVariables);
        response.setTimestamp(LocalDateTime.now());
        response.setPath(request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle Authentication Exceptions
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {

        logger.error(
                "AuthenticationException: Message: {}, Path: {}",
                ex.getMessage(),
                request.getDescription(false),
                ex);

        ApiErrorResponse response = new ApiErrorResponse();
        response.setErrorCode(HttpStatus.UNAUTHORIZED.name());
        response.setErrorMessage(messageResolver.getMessage("error.auth.unauthorized"));
        response.setTimestamp(LocalDateTime.now());
        response.setPath(request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // Handle Optimistic Locking & Concurrency Failures
    @ExceptionHandler({
        org.springframework.dao.ConcurrencyFailureException.class,
        jakarta.persistence.OptimisticLockException.class
    })
    public ResponseEntity<ApiErrorResponse> handleOptimisticLockingFailure(
            Exception ex, WebRequest request) {

        String path = request.getDescription(false).replace("uri=", "");
        logger.warn(
                "OptimisticLockException: Concurrent update detected on path: {}. Message: {}",
                path,
                ex.getMessage());

        ApiErrorResponse response = new ApiErrorResponse();
        response.setErrorCode("CONCURRENT_UPDATE_CONFLICT");
        response.setErrorMessage(messageResolver.getMessage("error.concurrency.optimistic_lock"));
        response.setTimestamp(LocalDateTime.now());
        response.setPath(path);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // Handle Race Condition Database Unique Constraint Violations
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(
            org.springframework.dao.DataIntegrityViolationException ex, WebRequest request) {

        String path = request.getDescription(false).replace("uri=", "");
        logger.warn(
                "DataIntegrityViolationException: Unique constraint race condition on path: {}. Message: {}",
                path,
                ex.getMessage());

        ApiErrorResponse response = new ApiErrorResponse();
        response.setErrorCode("DATA_INTEGRITY_VIOLATION");
        response.setErrorMessage(messageResolver.getMessage("error.concurrency.data_integrity"));
        response.setTimestamp(LocalDateTime.now());
        response.setPath(path);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // Handle Unexpected Exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {

        logger.error(
                "Unexpected Exception: Message: {}, Path: {}",
                ex.getMessage(),
                request.getDescription(false),
                ex);

        ApiErrorResponse response = new ApiErrorResponse();
        response.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.name());
        response.setErrorMessage(messageResolver.getMessage("error.internal_server"));
        response.setTimestamp(LocalDateTime.now());
        response.setPath(request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
