package com.godaddy.evapi.controller;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import com.godaddy.evapi.model.FraudListModel;
import com.godaddy.evapi.model.OrganizationInputModel;
import com.godaddy.evapi.model.OrganizationListModel;
import com.godaddy.evapi.model.OrganizationModel;
import com.godaddy.evapi.service.HomoglyphService;
import com.godaddy.evapi.service.IFraudService;
import com.godaddy.evapi.service.ILoggingService;
import com.godaddy.evapi.service.IOrganizationService;
import com.godaddy.evapi.service.TestFraudService;
import com.godaddy.evapi.service.TestOrganizationService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.lang.Assert;

public class OrganizationControllerTest {
    @Mock
    IOrganizationService organizationService;
    
    @Mock
    ILoggingService loggingService;
    
    @Mock
    IFraudService fraudService;
    
    @Mock
    HttpServletRequest request;
    
    @Mock
    HomoglyphService homoglyphService;
    
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
    public void organizationControllerGetAllTest2() {
        Optional<Integer> optInt = Optional.of(-1);
        Optional<Integer> optInt2 = Optional.of(2000);
        when(organizationService.findAll(anyInt(), anyInt())).thenReturn(TestOrganizationService.generateOrganizationList());
        when(organizationService.findByVariableArguments(anyString(), anyInt(), anyInt())).thenReturn(TestOrganizationService.generateOrganizationList());
        ResponseEntity<Resource<OrganizationListModel>> response = orgController.GetOrganizationList(optInt, optInt2, "");
        assert(response.getStatusCode() == HttpStatus.OK);
        OrganizationListModel orgList = response.getBody().getContent();
        assertNotNull(orgList);
        assert(orgList.getOrganizations().get(0).getCommonName().equals("example.com"));
    }
    
    @Test
    public void organizationControllerGetAllTest3() {
        Optional<Integer> optInt = Optional.of(1);
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
        organization.setOrganizationName("Test Organization Name LLC");
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
    public void organizationControllerCollisionDetectSerialNumber() {
        when(organizationService.findBySerialNumber(anyString(), anyInt(), anyInt())).thenReturn(TestOrganizationService.generateOrganizationList());
        CollisionModel result = orgController.CollisionDetectBySerialNumber("");
        assert(result.isCollision());
    }
    
    @Test
    public void organizationControllerCollisionDetectNameSerialCountry() {
        when(organizationService.findByNameSerialNumberCountry(anyString(), anyString(), anyString(), anyInt(), anyInt())).thenReturn(TestOrganizationService.generateOrganizationList());
        CollisionModel result = orgController.CollisionDetectByNameSerialCountry("", "", "");
        assert(result.isCollision());
    }

    @Test
    public void organizationControllerCollisionDetectAll() {
        when(organizationService.findByNameSerialNumberCountryState(anyString(), anyString(), anyString(), anyString(), anyInt(), anyInt())).thenReturn(TestOrganizationService.generateOrganizationList());
        CollisionModel result = orgController.CollisionDetectByAll("", "", "", "");
        assert(result.isCollision());
    }

    @Test
    public void validationTest() {
        OrganizationInputModel organization = new OrganizationInputModel("Dave's cool websites llc", "example.com", "1234", "Los Angeles", "CA", "United States", "", 
                    "1234 N Main St.", new Date(), new Date());
        when(organizationService.findByOrganizationName(anyString(), anyInt(), anyInt())).thenReturn(null);
        when(organizationService.findByCommonName(anyString(), anyInt(), anyInt())).thenReturn(null);
        boolean result = orgController.validateNewRecord(organization);
        assert(result);
    }
    
    @Test
    public void validationSucessTest() {
        OrganizationInputModel organization = new OrganizationInputModel("Dave's cool websites llc", "example.com", "1234", "Los Angeles", "CA", "US", "", 
                    "1234 N Main St.", new Date(), new Date());
        when(organizationService.findByOrganizationName(anyString(), anyInt(), anyInt())).thenReturn(null);
        when(organizationService.findByCommonName(anyString(), anyInt(), anyInt())).thenReturn(null);
        boolean result = orgController.validateNewRecord(organization);
        assert(result);
    }
    
    @Test
    public void validationFailureTest() {
        OrganizationInputModel organization = new OrganizationInputModel("Good ol' websites", "example.com", "1234", "Los Angeles", "CA", "United States", "", 
                    "1234 N Main St.", new Date(), new Date());
        when(organizationService.findByOrganizationName(anyString(), anyInt(), anyInt())).thenReturn(null);
        when(organizationService.findByCommonName(anyString(), anyInt(), anyInt())).thenReturn(null);
        boolean result = orgController.validateNewRecord(organization);
        assert(result == false);
    }
    
    @Test
    public void validationFailureTest2() {
        OrganizationInputModel organization = new OrganizationInputModel("Dave's cool websites llc", "example.com", "1234", "Los Angeles", "CA", "'Murika", "", 
                    "1234 N Main St.", new Date(), new Date());
        when(organizationService.findByOrganizationName(anyString(), anyInt(), anyInt())).thenReturn(null);
        when(organizationService.findByCommonName(anyString(), anyInt(), anyInt())).thenReturn(null);
        boolean result = orgController.validateNewRecord(organization);
        assert(result == false);
    }
    
    @Test
    public void homoglyphDetectionTest() {
        String domain = "yahoo.com";
        when(homoglyphService.containsMixedAlphabets(anyString())).thenReturn(false);
        when(homoglyphService.convertHomoglyphs(anyString())).thenReturn(domain);
        List<String> result = orgController.ValidateDomain(domain);
        Assert.notEmpty(result);
        Assert.isTrue(result.get(0).equals("No issues detected"));
    }
    
    @Test
    public void homoglyphDetectionTestFailure() {
        String domain = "yaℎoo.com";
        when(homoglyphService.containsMixedAlphabets(anyString())).thenReturn(true);
        when(homoglyphService.convertHomoglyphs(anyString())).thenReturn("yahoo.com");
        List<String> result = orgController.ValidateDomain(domain);
        Assert.notEmpty(result);
        Assert.isTrue(!result.contains("No issues detected"));
    }
    
    @Test
    public void homoglyphDetectionTestFailureBlank() {
        String domain = "";
        when(homoglyphService.containsMixedAlphabets(anyString())).thenReturn(false);
        when(homoglyphService.convertHomoglyphs(anyString())).thenReturn(domain);
        List<String> result = orgController.ValidateDomain(domain);
        Assert.notEmpty(result);
        Assert.isTrue(!result.get(0).equals("No issues detected"));
    }
    
    @Test
    public void homoglyphDetectionTestSuccess2() {
        String domain = "yah00.com";
        when(homoglyphService.containsMixedAlphabets(anyString())).thenReturn(false);
        when(homoglyphService.convertHomoglyphs(anyString())).thenReturn(domain);
        List<String> result = orgController.ValidateDomain(domain);
        Assert.notEmpty(result);
        Assert.isTrue(result.contains("No issues detected"));
    }
    
    @Test
    public void topSitesTest() {
        when(homoglyphService.searchForTopSites(anyString())).thenReturn(new ArrayList<String>());
        List<String> result = orgController.ValidateDomainTopSites("example.com");
        Assert.notEmpty(result);
        Assert.isTrue(result.contains("No issues detected"));
    }
    
    @Test
    public void topSitesTestFailure() {
        List<String> returnResult = new ArrayList<String>();
        returnResult.add("Matches google.com");
        when(homoglyphService.searchForTopSites(anyString())).thenReturn(returnResult);
        List<String> result = orgController.ValidateDomainTopSites("google.com");
        Assert.notEmpty(result);
        Assert.isTrue(result.contains("Matches google.com"));
    }
    
    @Test
    public void fraudTestSuccess() {
        //when(organizationService.findByOrganizationName(anyString(), anyInt(), anyInt())).thenReturn(null);
        when(fraudService.findAll(anyInt(), anyInt())).thenReturn(TestFraudService.GenerateFraudListModel());
        List<String> result = orgController.ValidateFraudDomain("gragle.com");
        Assert.notEmpty(result);
        Assert.isTrue(result.contains("No issues detected"));
        result = orgController.ValidateFraudOrganization("Fraggle Rock");
        Assert.notEmpty(result);
        Assert.isTrue(result.contains("No issues detected"));

    }

    @Test
    public void fraudTestFailure() {
        when(fraudService.findAll(anyInt(), anyInt())).thenReturn(TestFraudService.GenerateFraudListModel());
        List<String> result = orgController.ValidateFraudDomain("google.com");
        Assert.notEmpty(result);
        Assert.isTrue(!result.contains("No issues detected"));
        result = orgController.ValidateFraudOrganization("citibank");
        Assert.notEmpty(result);
        Assert.isTrue(!result.contains("No issues detected"));
    }

    @Test
    public void fraudTestFailure2() {
        when(fraudService.findAll(anyInt(), anyInt())).thenReturn(new FraudListModel());
        List<String> result = orgController.ValidateFraudDomain("google.com");
        Assert.notEmpty(result);
        Assert.isTrue(result.contains("No issues detected"));
    }

    @Test
    public void fraudTestFailure3() {
        when(fraudService.findAll(anyInt(), anyInt())).thenReturn(null);
        List<String> result = orgController.ValidateFraudDomain("google.com");
        Assert.notEmpty(result);
        Assert.isTrue(result.contains("No issues detected"));
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