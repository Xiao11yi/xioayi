package com.olivia.xioayi.common;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException() {
        return ResponseEntity.status(ResultCode.UNAUTHORIZED.getCode())
                .body(ApiResponse.error(ResultCode.UNAUTHORIZED));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException() {
        return ResponseEntity.status(ResultCode.FORBIDDEN.getCode())
                .body(ApiResponse.error(ResultCode.FORBIDDEN));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoSuchElementException() {
        return ResponseEntity.status(ResultCode.NOT_FOUND.getCode())
                .body(ApiResponse.error(ResultCode.NOT_FOUND));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        return ResponseEntity.status(ResultCode.INTERNAL_ERROR.getCode())
                .body(ApiResponse.error(ResultCode.INTERNAL_ERROR));
    }
}
