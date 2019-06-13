package org.ohdsi.gisservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.ohdsi.gisservice.security.sso.InvalidSsoTokenException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.ResponseEntity.status;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(value = InvalidSsoTokenException.class)
    public ResponseEntity invalidJwtAuthentication(InvalidSsoTokenException ex, WebRequest request) {

        // TODO:
        // Make it working
        log.debug(ex.getMessage());
        return status(UNAUTHORIZED).build();
    }
}
