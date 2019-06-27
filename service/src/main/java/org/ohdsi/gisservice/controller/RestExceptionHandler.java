package org.ohdsi.gisservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.ohdsi.gisservice.security.sso.InvalidSsoTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.ResponseEntity.status;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(value = InvalidSsoTokenException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Unauthorized access")
    public void invalidJwtAuthentication(InvalidSsoTokenException ex, WebRequest request) {

    }
}
