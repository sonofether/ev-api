package com.godaddy.evapi.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.google.common.base.Optional;

public class BasicAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Optional<String> username = (Optional) authentication.getPrincipal();
        Optional<String> password = (Optional) authentication.getCredentials();

        if (!username.isPresent() || !password.isPresent()) {
            throw new BadCredentialsException("Invalid Domain User Credentials");
        }

        String basicAuth = username.toString();
        
        // TODO: Get these from the decoded basic auth string
        
        // Get creds from basic auth
        // Header is in the format "Basic 5tyc0uiDat4"
        // We need to extract data before decoding it back to original string
        String[] authParts = basicAuth.split("\\s+");
        String authInfo = authParts[1];
        
        // Decode the data back to original string
        byte[] bytes = null;
        bytes = java.util.Base64.getMimeDecoder().decode(authInfo);
        String decodedAuth = new String(bytes);
        String[] parts = decodedAuth.split(":");

        // TODO: Validate the decoded values
        
        // TODO: Figure out level of auth...
        
        // TODO: Generate the jwt
        
        // TODO: Store the correct values.
        PreAuthenticatedAuthenticationToken tokenAuth = new PreAuthenticatedAuthenticationToken(username, decodedAuth);
        tokenAuth.setAuthenticated(true);
        
        return tokenAuth;
    }
    
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
    
}
