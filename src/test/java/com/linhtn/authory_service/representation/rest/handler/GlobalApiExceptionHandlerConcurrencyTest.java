package com.linhtn.authory_service.representation.rest.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.persistence.OptimisticLockException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import com.linhtn.sharedkernel.representation.ApiErrorResponse;
import com.linhtn.web.i18n.MessageResolver;

class GlobalApiExceptionHandlerConcurrencyTest {

    private MessageResolver i18nHelper;
    private GlobalApiExceptionHandler exceptionHandler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        i18nHelper = mock(MessageResolver.class);
        exceptionHandler = new GlobalApiExceptionHandler(i18nHelper);
        webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/users/123");
    }

    @Test
    void shouldReturn409ConflictOnOptimisticLockException() {
        OptimisticLockException exception =
                new OptimisticLockException("Row was updated or deleted by another transaction");
        when(i18nHelper.getMessage("error.concurrency.optimistic_lock"))
                .thenReturn("Concurrent update conflict");

        ResponseEntity<ApiErrorResponse> response =
                exceptionHandler.handleOptimisticLockingFailure(exception, webRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CONCURRENT_UPDATE_CONFLICT", response.getBody().getErrorCode());
        assertEquals("Concurrent update conflict", response.getBody().getErrorMessage());
        assertEquals("/api/v1/users/123", response.getBody().getPath());
    }

    @Test
    void shouldReturn409ConflictOnConcurrencyFailureException() {
        ConcurrencyFailureException exception =
                new ConcurrencyFailureException("Optimistic lock failure");
        when(i18nHelper.getMessage("error.concurrency.optimistic_lock"))
                .thenReturn("Concurrent update conflict");

        ResponseEntity<ApiErrorResponse> response =
                exceptionHandler.handleOptimisticLockingFailure(exception, webRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("CONCURRENT_UPDATE_CONFLICT", response.getBody().getErrorCode());
    }

    @Test
    void shouldReturn409ConflictOnDataIntegrityViolationException() {
        DataIntegrityViolationException exception =
                new DataIntegrityViolationException(
                        "duplicate key value violates unique constraint");
        when(i18nHelper.getMessage("error.concurrency.data_integrity"))
                .thenReturn("Duplicate record created concurrently");

        ResponseEntity<ApiErrorResponse> response =
                exceptionHandler.handleDataIntegrityViolation(exception, webRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("DATA_INTEGRITY_VIOLATION", response.getBody().getErrorCode());
        assertEquals("Duplicate record created concurrently", response.getBody().getErrorMessage());
    }
}
