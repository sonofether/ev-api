package com.godaddy.evapi.security;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.util.UrlPathHelper;

import com.google.common.base.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
public class AuthenticationFilterTest {
    @InjectMocks
    private AuthenticationFilter authFilter;
    
    private UrlPathHelper urlHelper;
    private AuthenticationManager authManager;
    private Authentication authMock;
    private MockHttpServletRequest request;
    //private MockHttpServletResponse response;
    private HttpServletResponse response;
    private MockFilterChain chain;
    
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        urlHelper = Mockito.mock(UrlPathHelper.class);
        authManager = Mockito.mock(AuthenticationManager.class);
        authMock = Mockito.mock(Authentication.class);
        
        ReflectionTestUtils.setField(authFilter, "urlHelper", urlHelper, UrlPathHelper.class);
        ReflectionTestUtils.setField(authFilter, "authenticationManager", authManager, AuthenticationManager.class);
        
        when(authMock.isAuthenticated()).thenReturn(true);
        when(authManager.authenticate(any())).thenReturn(authMock);
        
        request = new MockHttpServletRequest();
        response = Mockito.mock(HttpServletResponse.class);//new MockHttpServletResponse();
        chain = new MockFilterChain();
        
        request.addHeader("X-Auth-Token", TokenAuthenticationProviderTest.generateToken(5));
        request.addHeader("Authorization", "Basic: dXNlcjpwYXNzd29yZA==");
    }
    
    @Test
    public void testBasicAuth() throws Exception {
        request.setMethod("POST");
        when(urlHelper.getPathWithinApplication(request)).thenReturn("/login");
        Mockito.doNothing().when(response).addHeader(anyString(), anyString());
        
        authFilter.doFilter(request, response, chain);
        assert(true);
    }
    
    @Test
    public void testTokenAuth() throws Exception {
        request.setMethod("GET");
        when(urlHelper.getPathWithinApplication(request)).thenReturn("organization");
 
        authFilter.doFilter(request, response, chain);
        assert(true);
    }
    
}
