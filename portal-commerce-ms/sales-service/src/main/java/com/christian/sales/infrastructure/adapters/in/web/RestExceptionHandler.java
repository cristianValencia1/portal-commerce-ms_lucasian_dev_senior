package com.christian.sales.infrastructure.adapters.in.web;

import com.christian.sales.application.dto.ErrorResponse;
import com.christian.sales.domain.exception.BusinessValidationException;
import com.christian.sales.domain.exception.ResourceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

        protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                                                                                  HttpHeaders headers,
                                                                                                                                  HttpStatusCode status,
                                                                                                                                  WebRequest request) {
                return buildValidationResponse(ex, headers, status, request);
        }

        protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                                                                                  HttpHeaders headers,
                                                                                                                                  HttpStatus status,
                                                                                                                                  WebRequest request) {
                return buildValidationResponse(ex, headers, status, request);
        }

    private ResponseEntity<Object> buildValidationResponse(MethodArgumentNotValidException ex,
                                                            HttpHeaders headers,
                                                            HttpStatusCode status,
                                                            WebRequest request) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("Validation failed")
                .details(details)
                .build();
        return handleExceptionInternal(ex, response, headers, status, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> details = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList());
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("Validation failed")
                .details(details)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .details(List.of())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(BusinessValidationException.class)
    public ResponseEntity<ErrorResponse> handleBusinessValidation(BusinessValidationException ex) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .details(List.of())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .details(List.of())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("Internal server error")
                .details(List.of(ex.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
