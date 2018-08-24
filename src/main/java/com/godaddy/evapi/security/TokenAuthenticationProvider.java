package com.godaddy.evapi.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.google.common.base.Optional;

public class TokenAuthenticationProvider implements AuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        
        Optional<String> token = (Optional) authentication.getPrincipal();
        if (!token.isPresent() || token.get().isEmpty()) {
            throw new BadCredentialsException("Invalid token");
        }
        
        // TODO: Decode and validate token
        
        // Need to get the cert we are using from config
        
        // TODO: Check the expires time from the token. Expired? Throw exception.
        
        // TODO: Get user vals from token? We want a service to handle the decode?
        
        // We passed! Set the auth values and bounce.
        // TODO: Set principal to the decoded token stuff so we can use it.
        PreAuthenticatedAuthenticationToken tokenAuth =  new PreAuthenticatedAuthenticationToken(token, token);
        tokenAuth.setAuthenticated(true);
        
        return tokenAuth;
    }
        
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(PreAuthenticatedAuthenticationToken.class);
    }

}
