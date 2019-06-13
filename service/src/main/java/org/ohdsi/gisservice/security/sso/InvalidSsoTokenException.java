package org.ohdsi.gisservice.security.sso;

import org.springframework.security.core.AuthenticationException;

public class InvalidSsoTokenException extends AuthenticationException {

    public InvalidSsoTokenException(String msg) {

        super(msg);
    }
}
