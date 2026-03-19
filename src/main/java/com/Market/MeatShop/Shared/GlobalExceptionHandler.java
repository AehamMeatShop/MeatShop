package com.Market.MeatShop.Shared;


import com.Market.MeatShop.Shared.Exceptions.AlreadyHaveSame;
import com.Market.MeatShop.Shared.Exceptions.TargetNotFound;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(TargetNotFound.class)
    public ResponseEntity<ErrorResponse> handleTargetNotFound(TargetNotFound ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse(ex.getMessage(), 404, LocalDateTime.now())
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErrorResponse("Value already exists", 409, LocalDateTime.now())
        );
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Unexpected error", 500, LocalDateTime.now())
        );
    }
}
