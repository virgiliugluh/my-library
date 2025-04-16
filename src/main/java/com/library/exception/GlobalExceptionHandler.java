package com.library.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;


@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        log.error("Validation exception", ex);

        final var errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        final var errorResponse = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), errors.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<ApiErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {

        log.error("User not found", ex);

        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler({BookNotFoundException.class})
    public ResponseEntity<ApiErrorResponse> handleBookNotFoundException(BookNotFoundException ex) {

        log.error("Book not found", ex);

        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler({BookAlreadyLoanedException.class})
    public ResponseEntity<ApiErrorResponse> handleBookAlreadyLoanedException(BookAlreadyLoanedException ex) {

        log.error("Book already loaned", ex);

        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler({LoanNotFoundException.class})
    public ResponseEntity<ApiErrorResponse> handleLoanNotFoundException(LoanNotFoundException ex) {

        log.error("Loan not found", ex);

        final var errorResponse = new ApiErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {

        log.error("Data integrity violation", ex);

        final var error = new ApiErrorResponse(HttpStatus.CONFLICT.value(), "Data integrity violation");

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex) {

        log.error("Unhandled exception occurred", ex);

        final var error = new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred."
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}