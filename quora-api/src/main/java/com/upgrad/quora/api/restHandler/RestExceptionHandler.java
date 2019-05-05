package com.upgrad.quora.api.exception;


import com.upgrad.quora.api.model.ErrorResponse;
import com.upgrad.quora.service.exception.*;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(SignOutRestrictedException.class)
  public ResponseEntity<ErrorResponse> resourceNotFoundException(
      SignOutRestrictedException exe, WebRequest request) {
    return new ResponseEntity<ErrorResponse>(
        new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()),
        HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(SignUpRestrictedException.class)
  public ResponseEntity<ErrorResponse> resourceAlreadyExistsException(
      SignUpRestrictedException exc, WebRequest webRequest) {
    return new ResponseEntity<ErrorResponse>(
        new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()),
        HttpStatus.CONFLICT);
  }

  @ExceptionHandler(AuthenticationFailedException.class)
  public ResponseEntity<ErrorResponse> resourceNotFoundException(
      AuthenticationFailedException exc, WebRequest webRequest) {
    return new ResponseEntity<ErrorResponse>(
        new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()),
        HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(AuthorizationFailedException.class)
  public ResponseEntity<ErrorResponse> resourceUnauthorizedException(
      AuthorizationFailedException exe, WebRequest webRequest) {
    return new ResponseEntity<ErrorResponse>(
        new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()),
        HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> resourceNotFoundException(
      UserNotFoundException exe, WebRequest webRequest) {
    return new ResponseEntity<ErrorResponse>(
        new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()),
        HttpStatus.NOT_FOUND);
  }
}

