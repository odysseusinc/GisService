package org.ohdsi.gisservice.security.sso;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class SsoFilter extends GenericFilterBean {

    private final SsoProvider ssoProvider;

    public SsoFilter(SsoProvider ssoProvider) {

        this.ssoProvider = ssoProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        String token = ((HttpServletRequest) request).getHeader(HttpHeaders.AUTHORIZATION);
        if (token != null) {
            Authentication auth = ssoProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }
}
