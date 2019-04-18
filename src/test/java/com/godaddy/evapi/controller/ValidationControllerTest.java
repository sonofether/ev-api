package com.godaddy.evapi.controller;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import com.godaddy.evapi.model.IdModel;
import com.godaddy.evapi.model.OrganizationListModel;
import com.godaddy.evapi.model.OrganizationModel;
import com.godaddy.evapi.model.ValidationInputModel;
import com.godaddy.evapi.model.ValidationItemModel;
import com.godaddy.evapi.model.ValidationListModel;
import com.godaddy.evapi.service.IFileService;
import com.godaddy.evapi.service.IOrganizationService;
import com.godaddy.evapi.service.IValidationService;
import com.godaddy.evapi.service.TestOrganizationService;
import com.godaddy.evapi.service.TestValidationService;

import io.jsonwebtoken.Claims;

public class ValidationControllerTest {

    @Mock
    IValidationService validationService;
    
    @Mock
    IOrganizationService organizationService;
    
    @Mock
    IFileService fileService;
    
    @InjectMocks
    private ValidationController validationController;
    
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(validationController, "basePath", "/tmp/");
        SetupAuthentication();
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
    public void validationControllerGetFailureTest() {
        when(validationService.findById(anyString())).thenReturn(null);
        ResponseEntity<ValidationItemModel> response = validationController.GetValidationById("1234");
        assert(response.getStatusCode() == HttpStatus.NOT_FOUND);
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
    public void validationControllerGetAllFailureTest() {
        Optional<Integer> optInt = Optional.empty();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("https://example.com/?offset=0&limit=25");
        request.setQueryString("?offset=0&limit=25");
        when(validationService.findAll(anyInt(), anyInt())).thenReturn(null);
        ResponseEntity<Resource<ValidationListModel>> response = validationController.GetValidationList(request, optInt, optInt);
        assert(response.getStatusCode() == HttpStatus.NOT_FOUND);
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
        ResponseEntity<HttpStatus> result = validationController.RemoveRecord("1234");
        assert(result.getStatusCode() == HttpStatus.OK);
    }
    
    @Test
    public void validationControllerDeleteFailureTest() {
        when(validationService.findById(anyString())).thenReturn(null);
        ResponseEntity<HttpStatus> result = validationController.RemoveRecord("1234");
        assert(result.getStatusCode() == HttpStatus.NOT_FOUND);
    }
    
    @Test
    public void validationControllerUpdateTest() {
        MockMultipartFile file = new MockMultipartFile("data", "filename.txt", "text/plain", "Test xml data".getBytes());
        when(validationService.findById(any())).thenReturn(TestValidationService.generateValidationItem());
        when(organizationService.findById(any())).thenReturn(TestOrganizationService.generateOrganization());
        when(fileService.uploadFile(any(), any())).thenReturn(true);
        when(validationService.save(any())).thenReturn(true);
        ResponseEntity<IdModel> response = validationController.UpdateRecord("1234", file);
        assert(response.getStatusCode() == HttpStatus.OK);
    }
    
    @Test
    public void validationControllerUpdateFailureTest() {
        MockMultipartFile file = new MockMultipartFile("data", "filename.txt", "text/plain", "Test xml data".getBytes());
        when(validationService.findById(any())).thenReturn(TestValidationService.generateValidationItem());
        when(organizationService.findById(any())).thenReturn(TestOrganizationService.generateOrganization());
        when(fileService.uploadFile(any(), any())).thenReturn(false);
        when(validationService.save(any())).thenReturn(true);
        ResponseEntity<IdModel> response = validationController.UpdateRecord("1234", file);
        assert(response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @Test
    public void validationControllerUpdateFetchFailureTest() {
        MockMultipartFile file = new MockMultipartFile("data", "filename.txt", "text/plain", "Test xml data".getBytes());
        when(validationService.findById(any())).thenReturn(TestValidationService.generateValidationItem());
        when(organizationService.findById(any())).thenReturn(null);
        when(fileService.uploadFile(any(), any())).thenReturn(true);
        when(validationService.save(any())).thenReturn(true);
        ResponseEntity<IdModel> response = validationController.UpdateRecord("1234", file);
        assert(response.getStatusCode() == HttpStatus.BAD_REQUEST);
    }
    
    @Test
    public void validationControllerUpdateRecordFailureTest() {
        MockMultipartFile file = new MockMultipartFile("data", "filename.txt", "text/plain", "Test xml data".getBytes());
        when(validationService.findById(any())).thenReturn(null);
        when(organizationService.findById(any())).thenReturn(TestOrganizationService.generateOrganization());
        when(fileService.uploadFile(any(), any())).thenReturn(false);
        when(validationService.save(any())).thenReturn(true);
        ResponseEntity<IdModel> response = validationController.UpdateRecord("1234", file);
        assert(response.getStatusCode() == HttpStatus.NOT_FOUND);
    }


    
    @Test
    public void validationControllerCreateTest() {
        ValidationInputModel validationItem = new ValidationInputModel();
        validationItem.setCertId(UUID.randomUUID());
        validationItem.setValidates("Owner");
        when(organizationService.findById(any())).thenReturn(TestOrganizationService.generateOrganization());
        when(validationService.save(any())).thenReturn(true);
        ResponseEntity<IdModel> response = validationController.AddValidationItem(validationItem);
        assert(response.getStatusCode() == HttpStatus.CREATED);
    }

    @Test
    public void validationControllerCreateFailureTest() {
        ValidationInputModel validationItem = new ValidationInputModel();
        validationItem.setCertId(UUID.randomUUID());
        validationItem.setValidates("Owner");
        when(organizationService.findById(any())).thenReturn(null);
        when(validationService.save(any())).thenReturn(true);
        ResponseEntity<IdModel> response = validationController.AddValidationItem(validationItem);
        assert(response.getStatusCode() == HttpStatus.BAD_REQUEST);
    }

    @Test
    public void validationControllerCreateWriteFailureTest() {
        ValidationInputModel validationItem = new ValidationInputModel();
        validationItem.setCertId(UUID.randomUUID());
        validationItem.setValidates("Owner");
        when(organizationService.findById(any())).thenReturn(TestOrganizationService.generateOrganization());
        when(validationService.save(any())).thenReturn(false);
        ResponseEntity<IdModel> response = validationController.AddValidationItem(validationItem);
        assert(response.getStatusCode() == HttpStatus.BAD_REQUEST);
    }


    // Private/Helper functions
    private void SetupAuthentication() {
        Claims claims = Mockito.mock(Claims.class);
        when(claims.get(anyString())).thenReturn("Cert Authority");
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getCredentials()).thenReturn(claims);
    }


}
