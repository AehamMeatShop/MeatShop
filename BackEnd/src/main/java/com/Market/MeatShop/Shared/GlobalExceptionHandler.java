package com.Market.MeatShop.Shared;

import com.Market.MeatShop.Shared.Exceptions.*;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(QuantityIsNotRegular.class)
  public ResponseEntity<?> handleQuantityIsNotRegularException(QuantityIsNotRegular ex) {

    log.warn("ERROR : the quantity is not regular {}", ex.getMessage());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(ex.getMessage(), 400, LocalDateTime.now()));
  }

  @ExceptionHandler(LoginFaildException.class)
  public ResponseEntity<?> handleLoginFaildException(LoginFaildException ex) {

    log.warn("ERROR : Login Faild  {}", ex.getMessage());

    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new ErrorResponse(ex.getMessage(), 403, LocalDateTime.now()));
  }

  // AuthorizationDeniedException

  @ExceptionHandler(AuthorizationDeniedException.class)
  public ResponseEntity<?> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {

    log.warn("ERROR : Un Authorized action  {}", ex.getMessage());

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ErrorResponse("Un Authorized", 401, LocalDateTime.now()));
  }

  @ExceptionHandler(SessionStolenException.class)
  public ResponseEntity<?> handleSessionStolenException(SessionStolenException ex) {

    log.warn(
        "ERROR : Login Faild this session is blocked and the user is traced  {}", ex.getMessage());

    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(
            new ErrorResponse(
                "The service is temporarily unavailable. Please try again later.",
                503,
                LocalDateTime.now()));
  }

  @ExceptionHandler(ExpiredJwtException.class)
  public ResponseEntity<?> handleExpiredJwtException(ExpiredJwtException ex) {

    log.warn("ERROR : access token expired {}", ex.getMessage());

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(
            new ErrorResponse(
                "The access token has expired refresh it or re log in", 401, LocalDateTime.now()));
  }

  @ExceptionHandler(SessionExpiredException.class)
  public ResponseEntity<?> handleSessionExpiredException(SessionExpiredException ex) {

    log.warn("ERROR : try access to expired session {}", ex.getMessage());

    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new ErrorResponse(ex.getMessage(), 403, LocalDateTime.now()));
  }
@ExceptionHandler(RefreshErrorException.class)
public ResponseEntity<?> handleRefreshErrorException(RefreshErrorException ex) {

  log.warn("ERROR : some thing went wrong with refresh");

  return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(new ErrorResponse(ex.getMessage(), 403, LocalDateTime.now()));
}
  @ExceptionHandler(SessionNotFoundException.class)
  public ResponseEntity<?> handleSessionNotFoundException(SessionNotFoundException ex) {

    log.warn("ERROR : Login Faild  {}", ex.getMessage());

    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new ErrorResponse(ex.getMessage(), 403, LocalDateTime.now()));
  }

  @ExceptionHandler(AccountNotFounException.class)
  public ResponseEntity<?> handleAccountNotFounException(AccountNotFounException ex) {

    log.warn("ERROR : Account not found  {}", ex.getMessage());

    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new ErrorResponse(ex.getMessage(), 403, LocalDateTime.now()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
    log.warn("ERROR : Illegal argument: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(ex.getMessage(), 400, LocalDateTime.now()));
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<?> handleIllegalStateException(IllegalStateException ex) {
    log.warn("ERROR : wrong state {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse("wrong state ", 400, LocalDateTime.now()));
  }

  @ExceptionHandler(RatioError.class)
  public ResponseEntity<?> handleRatioError(RatioError ex) {
    log.warn("ERROR :{}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(ex.getMessage(), 400, LocalDateTime.now()));
  }

  @ExceptionHandler(SubjectProviderNotFoundException.class)
  public ResponseEntity<?> handleSubjectProviderNotFoundException(
      SubjectProviderNotFoundException ex) {
    log.warn("ERROR :{}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new ErrorResponse(ex.getMessage(), 403, LocalDateTime.now()));
  }

  @ExceptionHandler(AlreadyHaveComponents.class)
  public ResponseEntity<?> handleAlreadyHaveComponentsException(AlreadyHaveComponents ex) {
    log.warn("ERROR :{}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(ex.getMessage(), 400, LocalDateTime.now()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
    log.warn("ERROR: bad arguments {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse("bad arguments", 400, LocalDateTime.now()));
  }

  @ExceptionHandler(TypeError.class)
  public ResponseEntity<?> handleTypeError(TypeError ex) {
    log.warn("ERROR :{}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(ex.getMessage(), 400, LocalDateTime.now()));
  }

  @ExceptionHandler(TargetNotFound.class)
  public ResponseEntity<ErrorResponse> handleTargetNotFound(TargetNotFound ex) {
    log.warn("ERROR :{}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse(ex.getMessage(), 404, LocalDateTime.now()));
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {

    String message = "Database constraint violation";

    if (ex.getRootCause() != null && ex.getRootCause().getMessage().contains("unique")) {

      message = "This record already exists";

    } else if (ex.getRootCause() != null
        && ex.getRootCause().getMessage().contains("foreign key")) {

      message = "Cannot delete this record because it is linked to other data";
    }
    log.warn("ERROR :{} {}", message, ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ErrorResponse(message, 409, LocalDateTime.now()));
  }

  @ExceptionHandler(PasswordCompromisedException.class)
  public ResponseEntity<?> handleQuantityIsNotRegularException(PasswordCompromisedException ex) {

    log.warn("ERROR : the user try to rgester with compromised password {}", ex.getMessage());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(ex.getMessage(), 400, LocalDateTime.now()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
    log.error("UNEXPEXTED ERROR :  {}", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse("Unexpected error", 500, LocalDateTime.now()));
  }
}
