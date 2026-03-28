package com.Market.MeatShop.Shared;



import com.Market.MeatShop.Shared.Exceptions.*;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.rmi.AlreadyBoundException;
import java.time.LocalDateTime;



@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                body( new ErrorResponse(ex.getMessage(), 400, LocalDateTime.now()));

    }

    @ExceptionHandler(RatioError.class)
    public ResponseEntity<?> handleRatioError(RatioError ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponse(ex.getMessage(), 400, LocalDateTime.now()));
    }

    @ExceptionHandler(AlreadyHaveComponents.class)
    public ResponseEntity<?> handleAlreadyHaveComponentsException(AlreadyHaveComponents ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponse(ex.getMessage(), 400, LocalDateTime.now()));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponse( "bad arguments", 400, LocalDateTime.now())
        );
    }
    
    @ExceptionHandler(TypeError.class)
    public ResponseEntity<?> handleTypeError(TypeError ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponse(ex.getMessage(), 400, LocalDateTime.now())
        );
    }


    @ExceptionHandler(TargetNotFound.class)
    public ResponseEntity<ErrorResponse> handleTargetNotFound(TargetNotFound ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse(ex.getMessage(), 404, LocalDateTime.now())
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {

        String message = "Database constraint violation";

        if (ex.getRootCause() != null &&
                ex.getRootCause().getMessage().contains("unique")) {

            message = "This record already exists";

        } else if (ex.getRootCause() != null &&
                ex.getRootCause().getMessage().contains("foreign key")) {

            message = "Cannot delete this record because it is linked to other data";
        }

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        message,
                        409,
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Unexpected error", 500, LocalDateTime.now())
        );
    }
}
