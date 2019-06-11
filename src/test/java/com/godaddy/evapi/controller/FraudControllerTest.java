package com.godaddy.evapi.controller;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.godaddy.evapi.model.FraudInputModel;
import com.godaddy.evapi.model.FraudListModel;
import com.godaddy.evapi.model.FraudModel;
import com.godaddy.evapi.model.IdModel;
import com.godaddy.evapi.service.IFraudService;
import com.godaddy.evapi.service.ILoggingService;
import com.godaddy.evapi.service.TestFraudService;

import io.jsonwebtoken.Claims;

public class FraudControllerTest {

    @Mock
    IFraudService fraudService;
    
    @Mock
    ILoggingService loggingService;
    
    @Mock
    HttpServletRequest request;
    
    @InjectMocks
    private FraudController fraudController;
    
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getRemoteHost()).thenReturn("127.0.0.1");
        when(request.getServerName()).thenReturn("www.example.com");
        when(request.getRequestURI()).thenReturn("/");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com/"));
        when(request.getQueryString()).thenReturn("");
        when(loggingService.insertLog(any())).thenReturn(true);
        SetupAuthentication();
    }
    
    @Test
    public void fraudControllerGetByIdTest() {
        when(fraudService.findById(anyString())).thenReturn(TestFraudService.GenerateFraudModel());
        ResponseEntity<FraudModel> response = fraudController.getById("id");
        FraudModel fm = response.getBody();
        assertNotNull(fm);
        assert(fm.getKeyword().equals("google.com"));
    }
    
    @Test
    public void fraudControllerGetByIdTestFailure() {
        when(fraudService.findById(anyString())).thenReturn(null);
        ResponseEntity<FraudModel> response = fraudController.getById("id");
        assert(response.getStatusCode() == HttpStatus.NOT_FOUND);
    }
    
    @Test
    public void fraudControllerGetAllTest() {
        Optional<Integer> optInt = Optional.empty();
        when(loggingService.insertLog(any())).thenReturn(true);
        when(fraudService.findAll(anyInt(), anyInt())).thenReturn(TestFraudService.GenerateFraudListModel());
        when(fraudService.findByVariableArguments(anyString(), anyInt(), anyInt())).thenReturn(TestFraudService.GenerateFraudListModel());
        ResponseEntity<Resource<FraudListModel>> response = fraudController.getAll(optInt, optInt, "");
        assert(response.getStatusCode() == HttpStatus.OK);
        FraudListModel fm = response.getBody().getContent();
        assertNotNull(fm);
        assert(fm.getFraudList().get(0).getKeyword().equals("google.com"));
    }
    
    @Test
    public void fraudControllerGetAllTestFailure() {
        Optional<Integer> optInt = Optional.empty();
        when(loggingService.insertLog(any())).thenReturn(true);
        when(fraudService.findAll(anyInt(), anyInt())).thenReturn(TestFraudService.GenerateFraudListModel());
        when(fraudService.findByVariableArguments(anyString(), anyInt(), anyInt())).thenReturn(null);
        ResponseEntity<Resource<FraudListModel>> response = fraudController.getAll(optInt, optInt, "");
        assert(response.getStatusCode() == HttpStatus.NOT_FOUND);
    }
    
    @Test
    public void fraudControllerGetAllTestFailure2() {
        Optional<Integer> optInt = Optional.empty();
        when(loggingService.insertLog(any())).thenReturn(true);
        when(fraudService.findAll(anyInt(), anyInt())).thenReturn(new FraudListModel());
        when(fraudService.findByVariableArguments(anyString(), anyInt(), anyInt())).thenReturn(null);
        ResponseEntity<Resource<FraudListModel>> response = fraudController.getAll(optInt, optInt, "");
        assert(response.getStatusCode() == HttpStatus.NOT_FOUND);
    }
    
    @Test
    public void fraudControllerDeleteTest() {
        when(fraudService.findById(anyString())).thenReturn(TestFraudService.GenerateFraudModel());
        when(fraudService.delete(anyString())).thenReturn(true);
        ResponseEntity<HttpStatus> response = fraudController.deleteFraud("id");
        assert(response.getStatusCode() == HttpStatus.OK);
    }
    
    @Test
    public void fraudControllerDeleteFailure() {
        when(fraudService.findById(anyString())).thenReturn(TestFraudService.GenerateFraudModel());
        when(fraudService.delete(anyString())).thenReturn(false);
        ResponseEntity<HttpStatus> response = fraudController.deleteFraud("id");
        assert(response.getStatusCode() == HttpStatus.BAD_REQUEST);
    }
    
    @Test
    public void fraudControllerDeleteFailure2() {
        when(fraudService.findById(anyString())).thenReturn(null);
        ResponseEntity<HttpStatus> response = fraudController.deleteFraud("id");
        assert(response.getStatusCode() == HttpStatus.BAD_REQUEST);
    }
    
    @Test
    public void fraudControllerCreateTest() {
        FraudInputModel fraudEntry = new FraudInputModel();
        fraudEntry.setDescription("description");
        fraudEntry.setKeyword("keyword");
        fraudEntry.setType(1);
        
        when(fraudService.findByKeyword(anyString(), anyInt(), anyInt())).thenReturn(null);
        when(fraudService.save(any())).thenReturn(true);
        
        ResponseEntity<IdModel> response = fraudController.createFraudEntry(fraudEntry);
        assert(response.getStatusCode() == HttpStatus.CREATED);
        assertNotNull(response.getBody());
        assert(response.getBody().toJson().length() > 0);
    }
    
    @Test
    public void fraudControllerCreateTestUpdate() {
        FraudInputModel fraudEntry = new FraudInputModel();
        fraudEntry.setDescription("description");
        fraudEntry.setKeyword("keyword");
        fraudEntry.setType(1);
        
        when(fraudService.findByKeyword(anyString(), anyInt(), anyInt())).thenReturn(TestFraudService.GenerateFraudListModel());
        when(fraudService.save(any())).thenReturn(true);
        
        ResponseEntity<IdModel> response = fraudController.createFraudEntry(fraudEntry);
        assert(response.getStatusCode() == HttpStatus.OK);
        assertNotNull(response.getBody());
        assert(response.getBody().toJson().length() > 0);
    }
    
    @Test
    public void fraudControllerCreateTestFailure() {
        FraudInputModel fraudEntry = new FraudInputModel();
        fraudEntry.setDescription("description");
        fraudEntry.setKeyword("keyword");
        fraudEntry.setType(1);
        
        when(fraudService.findByKeyword(anyString(), anyInt(), anyInt())).thenReturn(null);
        when(fraudService.save(any())).thenReturn(false);
        
        ResponseEntity<IdModel> response = fraudController.createFraudEntry(fraudEntry);
        assert(response.getStatusCode() == HttpStatus.BAD_REQUEST);
    }
    
    @Test
    public void fraudControllerCreateTestFailure2() {
        FraudInputModel fraudEntry = new FraudInputModel();
        fraudEntry.setDescription("");
        fraudEntry.setKeyword("");
        fraudEntry.setType(1);
        
        when(fraudService.findByKeyword(anyString(), anyInt(), anyInt())).thenReturn(null);
        when(fraudService.save(any())).thenReturn(false);
        
        ResponseEntity<IdModel> response = fraudController.createFraudEntry(fraudEntry);
        assert(response.getStatusCode() == HttpStatus.BAD_REQUEST);
        
    }
    
    @Test
    public void fraudControllerCreateTestFailure3() {
        FraudInputModel fraudEntry = new FraudInputModel();
        fraudEntry.setDescription("description");
        fraudEntry.setKeyword("keyword");
        fraudEntry.setType(-1);
        
        when(fraudService.findByKeyword(anyString(), anyInt(), anyInt())).thenReturn(null);
        when(fraudService.save(any())).thenReturn(false);
        
        ResponseEntity<IdModel> response = fraudController.createFraudEntry(fraudEntry);
        assert(response.getStatusCode() == HttpStatus.BAD_REQUEST);
    }
    
    @Test
    public void fraudControllerUpdateTest() {
        ResponseEntity<HttpStatus> response = fraudController.updateFraud("");
        assert(response.getStatusCode() == HttpStatus.NOT_IMPLEMENTED);
    }
    
    // Private/Helper functions
    
    private void SetupAuthentication() {
        Claims claims = Mockito.mock(Claims.class);
        when(claims.get(anyString())).thenReturn("My Cool CA");
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getCredentials()).thenReturn(claims);
    }
}
