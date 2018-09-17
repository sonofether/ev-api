package com.godaddy.evapi.controller;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
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
        String result = loginController.authenticateUser("", response);
        assertNotNull(result);
    }
}
