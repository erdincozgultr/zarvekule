package com.zarvekule.exceptions;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException; // Bu import önemli
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ExceptionResponse> handleApiException(ApiException apiException) {
        return new ResponseEntity<>(
                new ExceptionResponse(apiException.getMessage(), apiException.getHttpStatus().value(), LocalDateTime.now()),
                apiException.getHttpStatus());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException exception) {
        return new ResponseEntity<>(
                new ExceptionResponse("Bu işlemi yapmaya yetkiniz yok (Erişim Reddedildi).", HttpStatus.FORBIDDEN.value(), LocalDateTime.now()),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException(ConstraintViolationException constraintViolationException) {
        return new ResponseEntity<>(
                new ExceptionResponse(constraintViolationException.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGlobalException(Exception exception) {
        log.error("Beklenmeyen Hata: ", exception);

        return new ResponseEntity<>(
                new ExceptionResponse(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}