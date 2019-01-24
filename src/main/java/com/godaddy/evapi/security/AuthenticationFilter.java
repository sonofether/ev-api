package com.godaddy.evapi.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UrlPathHelper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;

public class AuthenticationFilter extends GenericFilterBean {
    
    private final static Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    
    private AuthenticationManager authenticationManager;
    private UrlPathHelper urlHelper;
    
    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        this.urlHelper = new UrlPathHelper();
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        Optional<String> basicAuth = Optional.fromNullable(httpRequest.getHeader("Authorization"));
        Optional<String> token = Optional.fromNullable(httpRequest.getHeader("X-Auth-Token"));
        
        String resourcePath = urlHelper.getPathWithinApplication(httpRequest);

        try {
            if (postToLogin(httpRequest, resourcePath)) {
                logger.debug("Trying to authenticate user {} by Basic Authentication method", basicAuth);
                // Do basic auth and issue a jwt
                validateBasicAuth(httpResponse, basicAuth);
                return;
            } else if (token.isPresent()) {
                logger.debug("Trying to authenticate user by X-Auth-Token method. Token: {}", token);
                // Do jwt auth
                validateToken(token);
            }
            
            chain.doFilter(request, response);
        }
        catch (AuthenticationException aex) {
            // Something went wrong. Do not let them in.
            SecurityContextHolder.clearContext();
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
    
    private void validateBasicAuth(HttpServletResponse httpResponse, Optional<String> basicAuth) {       
        UsernamePasswordAuthenticationToken requestAuthentication = new UsernamePasswordAuthenticationToken(basicAuth, null);
        Authentication resultOfAuthentication = authenticationManager.authenticate(requestAuthentication);
        if (resultOfAuthentication == null || !resultOfAuthentication.isAuthenticated()) {
            throw new InternalAuthenticationServiceException("Unable to authenticate Domain User for provided credentials");
        }
                   
        SecurityContextHolder.getContext().setAuthentication(resultOfAuthentication);
        httpResponse.setStatus(HttpServletResponse.SC_OK);

        // Store the auth token in the headers
        String token = (String)resultOfAuthentication.getCredentials();
        httpResponse.addHeader("X-Auth-Token", token);
        
        return;
    }
    
    private void validateToken(Optional<String> token) {
        PreAuthenticatedAuthenticationToken requestAuthentication = new PreAuthenticatedAuthenticationToken(token, null);
        Authentication responseAuthentication = authenticationManager.authenticate(requestAuthentication);
        if (responseAuthentication == null || !responseAuthentication.isAuthenticated()) {
            throw new InternalAuthenticationServiceException("Unable to authenticate Domain User for provided credentials");
        }
        
        SecurityContextHolder.getContext().setAuthentication(responseAuthentication);
        return;
    }
    
    public boolean postToLogin(HttpServletRequest httpRequest, String path) {
        return httpRequest.getMethod().equals("POST") && path.equalsIgnoreCase("/login");
    }
}
