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
    
    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        Optional<String> basicAuth = Optional.fromNullable(httpRequest.getHeader("Authorization"));
        Optional<String> token = Optional.fromNullable(httpRequest.getHeader("X-Auth-Token"));
        
        String resourcePath = new UrlPathHelper().getPathWithinApplication(httpRequest);

        try {
            if (postToLogin(httpRequest, resourcePath)) {
                logger.debug("Trying to authenticate user {} by X-Auth-Username method", basicAuth);
                // Do basic auth stuff
                validateBasicAuth(httpResponse, basicAuth);
                return;
            } else if (token.isPresent()) {
                logger.debug("Trying to authenticate user by X-Auth-Token method. Token: {}", token);
                // Do token auth
                validateToken(token);
            }
            
            logger.debug("AuthenticationFilter is passing request down the filter chain");
            chain.doFilter(request, response);
        }
        catch (InternalAuthenticationServiceException iasex) {
            SecurityContextHolder.clearContext();
            httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        catch (AuthenticationException aex) {
            SecurityContextHolder.clearContext();
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, aex.getMessage());
        }
    }
    
    private void validateBasicAuth(HttpServletResponse httpResponse, Optional<String> basicAuth) {       
        UsernamePasswordAuthenticationToken requestAuthentication = new UsernamePasswordAuthenticationToken(basicAuth, basicAuth);
        Authentication resultOfAuthentication = authenticationManager.authenticate(requestAuthentication);
        if (resultOfAuthentication == null || !resultOfAuthentication.isAuthenticated()) {
            throw new InternalAuthenticationServiceException("Unable to authenticate Domain User for provided credentials");
        }
                   
        SecurityContextHolder.getContext().setAuthentication(resultOfAuthentication);
        httpResponse.setStatus(HttpServletResponse.SC_OK);
        
        // TODO: Build token and stick into X-Auth-Token header
        String token = "TESTING";
        // TODO: Eventually the jwt will be stored here. We will convert to a string and send back in the header.
        token = (String)resultOfAuthentication.getCredentials();
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
        // TODO: Handle this correctly
        //return httpRequest.getMethod().equals("POST") && path.equalsIgnoreCase("login");
        return httpRequest.getMethod().equals("POST") && path.contains("login");
    }
}
