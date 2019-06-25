package org.ohdsi.gisservice.security.sso;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class SsoConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final SsoProvider ssoProvider;

    public SsoConfigurer(SsoProvider ssoProvider) {

        this.ssoProvider = ssoProvider;
    }

    @Override
    public void configure(HttpSecurity http) {

        SsoFilter ssoFilter = new SsoFilter(ssoProvider);
        http.addFilterBefore(ssoFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
