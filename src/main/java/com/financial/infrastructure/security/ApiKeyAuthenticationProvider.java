package com.financial.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {

    @Value("${app.api.key:demo-api-key-12345}")
    private String validApiKey;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String apiKey = (String) authentication.getCredentials();
        
        if (!validApiKey.equals(apiKey)) {
            throw new BadCredentialsException("Invalid API Key");
        }
        
        return new ApiKeyAuthenticationToken(apiKey, 
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_API_USER")));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiKeyAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
