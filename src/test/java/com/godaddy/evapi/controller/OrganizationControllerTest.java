package com.godaddy.evapi.controller;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Date;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.godaddy.evapi.model.CollisionModel;
import com.godaddy.evapi.model.OrganizationInputModel;
import com.godaddy.evapi.model.OrganizationListModel;
import com.godaddy.evapi.model.OrganizationModel;
import com.godaddy.evapi.service.ILoggingService;
import com.godaddy.evapi.service.IOrganizationService;
import com.godaddy.evapi.service.TestOrganizationService;

import io.jsonwebtoken.Claims;

public class OrganizationControllerTest {
    @Mock
    IOrganizationService organizationService;
    
    @Mock
    ILoggingService loggingService;
    
    @Mock
    HttpServletRequest request;
    
    @InjectMocks
    private OrganizationController orgController;
    
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
    public void organizationControllerGetTest() {
        when(organizationService.findById(anyString())).thenReturn(TestOrganizationService.generateOrganization());
        ResponseEntity<OrganizationModel> response = orgController.GetOrganization("1234");
        assert(response.getStatusCode() == HttpStatus.OK);
        OrganizationModel org = response.getBody();
        assertNotNull(org);
        assert(org.getCommonName().equals("example.com"));
    }
    
    @Test
    public void organizationControllerGetFailureTest() {
        when(organizationService.findById(anyString())).thenReturn(null);
        ResponseEntity<OrganizationModel> response = orgController.GetOrganization("1234");
        assert(response.getStatusCode() == HttpStatus.NOT_FOUND);
    }
    
    @Test
    public void organizationControllerGetAllTest() {
        Optional<Integer> optInt = Optional.empty();
        when(organizationService.findAll(anyInt(), anyInt())).thenReturn(TestOrganizationService.generateOrganizationList());
        when(organizationService.findByVariableArguments(anyString(), anyInt(), anyInt())).thenReturn(TestOrganizationService.generateOrganizationList());
        ResponseEntity<Resource<OrganizationListModel>> response = orgController.GetOrganizationList(optInt, optInt, "");
        assert(response.getStatusCode() == HttpStatus.OK);
        OrganizationListModel orgList = response.getBody().getContent();
        assertNotNull(orgList);
        assert(orgList.getOrganizations().get(0).getCommonName().equals("example.com"));
    }
    
    @Test
    public void organizationControllerGetAllFailureTest() {
        Optional<Integer> optInt = Optional.empty();
        when(organizationService.findAll(anyInt(), anyInt())).thenReturn(new OrganizationListModel());
        when(organizationService.findByVariableArguments(anyString(), anyInt(), anyInt())).thenReturn(new OrganizationListModel());
        ResponseEntity<Resource<OrganizationListModel>> response = orgController.GetOrganizationList(optInt, optInt, "");
        assert(response.getStatusCode() == HttpStatus.NOT_FOUND);
    }

    @Test
    public void organizationControllerGetAllFailureTest2() {
        Optional<Integer> optInt = Optional.empty();
        when(organizationService.findAll(anyInt(), anyInt())).thenReturn(new OrganizationListModel());
        when(organizationService.findByVariableArguments(anyString(), anyInt(), anyInt())).thenReturn(null);
        ResponseEntity<Resource<OrganizationListModel>> response = orgController.GetOrganizationList(optInt, optInt, "");
        assert(response.getStatusCode() == HttpStatus.NOT_FOUND);
    }

    @Test
    public void organizationControllerGetBySerialNumberTest() {
        Optional<Integer> optInt = Optional.empty();
        when(organizationService.findBySerialNumber(anyString(), anyInt(), anyInt())).thenReturn(TestOrganizationService.generateOrganizationList());
        ResponseEntity<Resource<OrganizationListModel>> response = orgController.GetOrganizationBySerialNumber("1234", optInt, optInt);
        assert(response.getStatusCode() == HttpStatus.OK);
        OrganizationListModel orgList = response.getBody().getContent();
        assertNotNull(orgList);
        assert(orgList.getOrganizations().get(0).getCommonName().equals("example.com"));
    }
    
    @Test
    public void organizationControllerGetBySerialNumberFailureTest() {
        Optional<Integer> optInt = Optional.empty();
        when(organizationService.findBySerialNumber(anyString(), anyInt(), anyInt())).thenReturn(new OrganizationListModel());
        ResponseEntity<Resource<OrganizationListModel>> response = orgController.GetOrganizationBySerialNumber("1234", optInt, optInt);
        assert(response.getStatusCode() == HttpStatus.NOT_FOUND);
    }

    @Test
    public void organizationControllerGetBySerialNumberFailureTest2() {
        Optional<Integer> optInt = Optional.empty();
        when(organizationService.findBySerialNumber(anyString(), anyInt(), anyInt())).thenReturn(null);
        ResponseEntity<Resource<OrganizationListModel>> response = orgController.GetOrganizationBySerialNumber("1234", optInt, optInt);
        assert(response.getStatusCode() == HttpStatus.NOT_FOUND);
    }
    
    @Test
    public void organizationControllerGetByCommonNameTest() {
        Optional<Integer> optInt = Optional.empty();
        when(organizationService.findByCommonName(anyString(), anyInt(), anyInt())).thenReturn(TestOrganizationService.generateOrganizationList());
        ResponseEntity<Resource<OrganizationListModel>> response = orgController.GetOrganizationByCommonName("example.com", optInt, optInt);
        assert(response.getStatusCode() == HttpStatus.OK);
        OrganizationListModel orgList = response.getBody().getContent();
        assertNotNull(orgList);
        assert(orgList.getOrganizations().get(0).getCommonName().equals("example.com"));
    }
    
    @Test
    public void organizationControllerGetByCommonNameFailureTest() {
        Optional<Integer> optInt = Optional.empty();
        when(organizationService.findByCommonName(anyString(), anyInt(), anyInt())).thenReturn(new OrganizationListModel());
        ResponseEntity<Resource<OrganizationListModel>> response = orgController.GetOrganizationByCommonName("example.com", optInt, optInt);
        assert(response.getStatusCode() == HttpStatus.NOT_FOUND);
    }
    
    @Test
    public void organizationControllerGetByCommonNameFailureTest2() {
        Optional<Integer> optInt = Optional.empty();
        when(organizationService.findByCommonName(anyString(), anyInt(), anyInt())).thenReturn(null);
        ResponseEntity<Resource<OrganizationListModel>> response = orgController.GetOrganizationByCommonName("example.com", optInt, optInt);
        assert(response.getStatusCode() == HttpStatus.NOT_FOUND);
    }

    @Test
    public void organizationControllerGetByOrganizationNameTest() {
        Optional<Integer> optInt = Optional.empty();
        when(organizationService.findByOrganizationName(anyString(), anyInt(), anyInt())).thenReturn(TestOrganizationService.generateOrganizationList());
        ResponseEntity<Resource<OrganizationListModel>> response = orgController.GetOrganizationByName("Asink, Inc", optInt, optInt);
        assert(response.getStatusCode() == HttpStatus.OK);
        OrganizationListModel orgList = response.getBody().getContent();
        assertNotNull(orgList);
        assert(orgList.getOrganizations().get(0).getCommonName().equals("example.com"));
    }
    
    @Test
    public void organizationControllerGetByOrganizationNameFailureTest() {
        Optional<Integer> optInt = Optional.empty();
        when(organizationService.findByOrganizationName(anyString(), anyInt(), anyInt())).thenReturn(new OrganizationListModel());
        ResponseEntity<Resource<OrganizationListModel>> response = orgController.GetOrganizationByName("Asink, Inc", optInt, optInt);
        assert(response.getStatusCode() == HttpStatus.NOT_FOUND);
    }
    
    @Test
    public void organizationControllerGetByOrganizationNameFailureTest2() {
        Optional<Integer> optInt = Optional.empty();
        when(organizationService.findByOrganizationName(anyString(), anyInt(), anyInt())).thenReturn(null);
        ResponseEntity<Resource<OrganizationListModel>> response = orgController.GetOrganizationByName("Asink, Inc", optInt, optInt);
        assert(response.getStatusCode() == HttpStatus.NOT_FOUND);
    }

    
    @Test
    public void organizationControllerGetByNameSerialNumberCountry() {
        Optional<Integer> optInt = Optional.empty();
        when(organizationService.findByNameSerialNumberCountry(anyString(), anyString(), anyString(), anyInt(), anyInt())).thenReturn(TestOrganizationService.generateOrganizationList());
        ResponseEntity<Resource<OrganizationListModel>> response = orgController.GetOrganizationByNameSerialNumberCountry("example.com", "1234", "US", optInt, optInt);
        assert(response.getStatusCode() == HttpStatus.OK);
        OrganizationListModel orgList = response.getBody().getContent();
        assertNotNull(orgList);
        assert(orgList.getOrganizations().get(0).getCommonName().equals("example.com"));
    }

    @Test
    public void organizationControllerGetByNameSerialNumberCountryFailure() {
        Optional<Integer> optInt = Optional.empty();
        when(organizationService.findByNameSerialNumberCountry(anyString(), anyString(), anyString(), anyInt(), anyInt())).thenReturn(new OrganizationListModel());
        ResponseEntity<Resource<OrganizationListModel>> response = orgController.GetOrganizationByNameSerialNumberCountry("example.com", "1234", "US", optInt, optInt);
        assert(response.getStatusCode() == HttpStatus.NOT_FOUND);
    }

    @Test
    public void organizationControllerGetByNameSerialNumberCountryFailure2() {
        Optional<Integer> optInt = Optional.empty();
        when(organizationService.findByNameSerialNumberCountry(anyString(), anyString(), anyString(), anyInt(), anyInt())).thenReturn(null);
        ResponseEntity<Resource<OrganizationListModel>> response = orgController.GetOrganizationByNameSerialNumberCountry("example.com", "1234", "US", optInt, optInt);
        assert(response.getStatusCode() == HttpStatus.NOT_FOUND);
    }

    
    @Test
    public void organizationControllerGetByNameSerialNumberCountryState() {
        Optional<Integer> optInt = Optional.empty();
        when(organizationService.findByNameSerialNumberCountryState(anyString(), anyString(), anyString(), anyString(), anyInt(), anyInt())).thenReturn(TestOrganizationService.generateOrganizationList());
        ResponseEntity<Resource<OrganizationListModel>> response = orgController.GetOrganizationByNameSerialNumberCountryState("example.com", "1234", "US", "AZ", optInt, optInt);
        assert(response.getStatusCode() == HttpStatus.OK);
        OrganizationListModel orgList = response.getBody().getContent();
        assertNotNull(orgList);
        assert(orgList.getOrganizations().get(0).getCommonName().equals("example.com"));
    }

    @Test
    public void organizationControllerGetByNameSerialNumberCountryStateFailure() {
        Optional<Integer> optInt = Optional.empty();
        when(organizationService.findByNameSerialNumberCountryState(anyString(), anyString(), anyString(), anyString(), anyInt(), anyInt())).thenReturn(new OrganizationListModel());
        ResponseEntity<Resource<OrganizationListModel>> response = orgController.GetOrganizationByNameSerialNumberCountryState("example.com", "1234", "US", "AZ", optInt, optInt);
        assert(response.getStatusCode() == HttpStatus.NOT_FOUND);
    }

    @Test
    public void organizationControllerGetByNameSerialNumberCountryStateFailure2() {
        Optional<Integer> optInt = Optional.empty();
        when(organizationService.findByNameSerialNumberCountryState(anyString(), anyString(), anyString(), anyString(), anyInt(), anyInt())).thenReturn(null);
        ResponseEntity<Resource<OrganizationListModel>> response = orgController.GetOrganizationByNameSerialNumberCountryState("example.com", "1234", "US", "AZ", optInt, optInt);
        assert(response.getStatusCode() == HttpStatus.NOT_FOUND);
    }

    
    // TODO Finish unit tests once writes are working
    @Test
    public void organizationControllerSaveTest() {
        /*
        Optional<Integer> optInt = Optional.empty();
        Authentication authMock = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authMock);
        Claims claims = new Claims();
        when(authMock.getCredentials()).thenReturn();
        //SecurityContextHolder.getContext().getAuthentication().getCredentials();
        */
        /*
        when(organizationService.save(any())).thenReturn(true);
        OrganizationInputModel organization = new OrganizationInputModel();
        organization.setCommonName("testcommonname.org");
        organization.setCountryName("US");
        organization.setExpirationDate(new Date());
        organization.setIssuedDate(new Date());
        organization.setLocalityName("Tempe");
        organization.setOrganizationName("Test Organization Name");
        organization.setSerialNumber("serialNumber");
        organization.setStateOrProvinceName("AZ");
        ResponseEntity<String> result = orgController.AddOrganization(organization);
        assert(result.getStatusCode() == HttpStatus.OK);
        */
    }
    
    @Test
    public void organizationControllerDeleteTest() {
        ResponseEntity<HttpStatus> result = orgController.DeleteOrganization("");
        assert(result.getStatusCode() == HttpStatus.NOT_IMPLEMENTED);
    }
    
    @Test
    public void organizationControllerUpdateTest() {
        ResponseEntity<HttpStatus> result = orgController.UpdateOrganization("");
        assert(result.getStatusCode() == HttpStatus.NOT_IMPLEMENTED);
    }
    
    @Test
    public void organizationControllerCollisionDetectTest() {
        when(organizationService.findByOrganizationName(anyString(), anyInt(), anyInt())).thenReturn(TestOrganizationService.generateOrganizationList());
        CollisionModel result = orgController.CollisionDetectByOrganizationName("");
        assert(result.isCollision());
    }
    
    @Test
    public void organizationControllerCollisionDetectCommonNameTest() {
        when(organizationService.findByCommonName(anyString(), anyInt(), anyInt())).thenReturn(TestOrganizationService.generateOrganizationList());
        CollisionModel result = orgController.CollisionDetectByCommonName("");
        assert(result.isCollision());
    }

    @Test
    public void dummyTest() {
        
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