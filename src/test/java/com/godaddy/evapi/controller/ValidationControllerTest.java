package com.godaddy.evapi.controller;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.godaddy.evapi.model.OrganizationListModel;
import com.godaddy.evapi.model.OrganizationModel;
import com.godaddy.evapi.model.ValidationItemModel;
import com.godaddy.evapi.model.ValidationListModel;
import com.godaddy.evapi.service.IValidationService;
import com.godaddy.evapi.service.TestOrganizationService;
import com.godaddy.evapi.service.TestValidationService;

import io.jsonwebtoken.Claims;

public class ValidationControllerTest {

    @Mock
    IValidationService validationService;
    
    @InjectMocks
    private ValidationController validationController;
    
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void validationControllerGetTest() {
        when(validationService.findById(anyString())).thenReturn(TestValidationService.generateValidationItem());
        ResponseEntity<ValidationItemModel> response = validationController.GetValidationById("1234");
        assert(response.getStatusCode() == HttpStatus.OK);
        ValidationItemModel validationItem = response.getBody();
        assertNotNull(validationItem);
        assert(validationItem.getValidates().equals("owner"));
    }
    
    @Test
    public void validationControllerGetAllTest() {
        Optional<Integer> optInt = Optional.empty();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("https://example.com/?offset=0&limit=25");
        request.setQueryString("?offset=0&limit=25");
        when(validationService.findAll(anyInt(), anyInt())).thenReturn(TestValidationService.generateValidationList());
        ResponseEntity<Resource<ValidationListModel>> response = validationController.GetValidationList(request, optInt, optInt);
        assert(response.getStatusCode() == HttpStatus.OK);
        ValidationListModel validationList = response.getBody().getContent();
        assertNotNull(validationList);
        assert(validationList.getValidationItems().get(0).getValidates().equals("owner"));        
    }
    
    @Test
    public void validationControllerGetByCertId() {
        Optional<Integer> optInt = Optional.empty();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("https://example.com/?offset=0&limit=25");
        request.setQueryString("?offset=0&limit=25");
        when(validationService.findByCertificateId(anyString(), anyInt(), anyInt())).thenReturn(TestValidationService.generateValidationList());
        ResponseEntity<Resource<ValidationListModel>> response = validationController.GetValidationItems(request, "1234", optInt, optInt);
        assert(response.getStatusCode() == HttpStatus.OK);
        ValidationListModel validationList = response.getBody().getContent();
        assertNotNull(validationList);
        assert(validationList.getValidationItems().get(0).getValidates().equals("owner"));        
    }
    
    @Test
    public void validationControllerGetByCertIdFailure() {
        Optional<Integer> optInt = Optional.empty();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("https://example.com/?offset=0&limit=25");
        request.setQueryString("?offset=0&limit=25");
        when(validationService.findByCertificateId(anyString(), anyInt(), anyInt())).thenReturn(null);
        ResponseEntity<Resource<ValidationListModel>> response = validationController.GetValidationItems(request, "1234", optInt, optInt);
        assert(response.getStatusCode() == HttpStatus.NOT_FOUND);
    }
    
    @Test
    public void validationControllerDeleteTest() {
        when(validationService.findById(anyString())).thenReturn(TestValidationService.generateValidationItem());
        when(validationService.save(any())).thenReturn(true);
        ResponseEntity<String> result = validationController.RemoveRecord("1234");
        assert(result.getStatusCode() == HttpStatus.OK);
    }
    
    @Test
    public void validationControllerDeleteFailureTest() {
        SetupAuthentication();
        when(validationService.findById(anyString())).thenReturn(null);
        ResponseEntity<String> result = validationController.RemoveRecord("1234");
        assert(result.getStatusCode() == HttpStatus.NOT_FOUND);
    }
    
    // TODO Finish unit tests once writes are working:
    @Test
    public void validationControllerUpdateTest() {
        SetupAuthentication();
    }
    
    @Test
    public void validationControllerCreateTest() {
        //AddValidationItem
        SetupAuthentication();
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
