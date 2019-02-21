package com.godaddy.evapi.controller;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;

public class LoginControllerTest {
    
    @InjectMocks
    private LoginController loginController;
    
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void loginControllerPostTest() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        ResponseEntity<HttpStatus> result = loginController.authenticateUser("", response);
        assertNotNull(result);
    }
}
