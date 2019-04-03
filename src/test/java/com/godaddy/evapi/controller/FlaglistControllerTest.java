package com.godaddy.evapi.controller;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

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

import com.godaddy.evapi.model.FlaglistInputModel;
import com.godaddy.evapi.model.FlaglistListModel;
import com.godaddy.evapi.model.FlaglistModel;
import com.godaddy.evapi.model.IdModel;
import com.godaddy.evapi.service.IFlaglistService;
import com.godaddy.evapi.service.ILoggingService;
import com.godaddy.evapi.service.TestFlaglistService;

import io.jsonwebtoken.Claims;

public class FlaglistControllerTest {
    @Mock
    IFlaglistService flaglistService;
    
    @Mock
    ILoggingService loggingService;
    
    @Mock
    HttpServletRequest request;
    
    @InjectMocks
    private FlaglistController flController;
    
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getServerName()).thenReturn("www.example.com");
        when(request.getRequestURI()).thenReturn("/");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com/"));
        when(request.getQueryString()).thenReturn("");
        when(loggingService.insertLog(any())).thenReturn(true);
    }
    
    @Test
    public void blacklistControllerGetByIdTest() {
        when(flaglistService.findById(anyString())).thenReturn(TestFlaglistService.generateFlaglist());
        ResponseEntity<FlaglistModel> response = flController.getById("1234");
        assert(response.getStatusCode() == HttpStatus.OK);
        FlaglistModel fl = response.getBody();
        assertNotNull(fl);
        assert(fl.getCommonName().equals("example.com"));
    }
    
    @Test
    public void blacklistControllerGetAll() {

        Optional<Integer> optInt = Optional.empty();
        when(loggingService.insertLog(any())).thenReturn(true);
        when(flaglistService.findAll(anyInt(), anyInt())).thenReturn(TestFlaglistService.generateFlaglistList());
        when(flaglistService.findByVariableArguments(anyString(), anyInt(), anyInt())).thenReturn(TestFlaglistService.generateFlaglistList());
        ResponseEntity<Resource<FlaglistListModel>> response = flController.getAll(optInt, optInt, "");
        assert(response.getStatusCode() == HttpStatus.OK);
        FlaglistListModel flList = response.getBody().getContent();
        assertNotNull(flList);
        assert(flList.getFlaglistEntries().get(0).getCommonName().equals("example.com"));
    }

    @Test
    public void blacklistControllerGetByCA() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("1.2.3.4");
        request.setServerName("www.example.com");
        request.setRequestURI("/");
        request.setQueryString("");
        Optional<Integer> optInt = Optional.empty();
        when(flaglistService.findByCA(anyString(), anyInt(), anyInt())).thenReturn(TestFlaglistService.generateFlaglistList());
        ResponseEntity<Resource<FlaglistListModel>> response = flController.getFlaglistByCA(optInt, optInt, "My CA");
        assert(response.getStatusCode() == HttpStatus.OK);
        FlaglistListModel flList = response.getBody().getContent();
        assertNotNull(flList);
        assert(flList.getFlaglistEntries().get(0).getCommonName().equals("example.com"));

    }
    
    @Test
    public void blacklistControllerGetByCName() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("1.2.3.4");
        request.setServerName("www.example.com");
        request.setRequestURI("/");
        request.setQueryString("");
        Optional<Integer> optInt = Optional.empty();
        when(flaglistService.findByCommonName(anyString(), anyInt(), anyInt())).thenReturn(TestFlaglistService.generateFlaglistList());
        ResponseEntity<Resource<FlaglistListModel>> response = flController.getFlaglistByCName(optInt, optInt, "example.com");
        assert(response.getStatusCode() == HttpStatus.OK);
        FlaglistListModel flList = response.getBody().getContent();
        assertNotNull(flList);
        assert(flList.getFlaglistEntries().get(0).getCommonName().equals("example.com"));

    }
    
    @Test
    public void blacklistControllerGetByOrgName() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("1.2.3.4");
        request.setServerName("www.example.com");
        request.setRequestURI("/");
        request.setQueryString("");
        Optional<Integer> optInt = Optional.empty();
        when(flaglistService.findByOrganizationName(anyString(), anyInt(), anyInt())).thenReturn(TestFlaglistService.generateFlaglistList());
        ResponseEntity<Resource<FlaglistListModel>> response = flController.getFlaglistByName(optInt, optInt, "organizagtion name");
        assert(response.getStatusCode() == HttpStatus.OK);
        FlaglistListModel flList = response.getBody().getContent();
        assertNotNull(flList);
        assert(flList.getFlaglistEntries().get(0).getCommonName().equals("example.com"));
    }
    
    @Test
    public void blacklistControllerGetBySource() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("1.2.3.4");
        request.setServerName("www.example.com");
        request.setRequestURI("/");
        request.setQueryString("");
        Optional<Integer> optInt = Optional.empty();
        when(flaglistService.findBySource(anyString(), anyInt(), anyInt())).thenReturn(TestFlaglistService.generateFlaglistList());
        ResponseEntity<Resource<FlaglistListModel>> response = flController.getFlaglistBySource(optInt, optInt, "example.com");
        assert(response.getStatusCode() == HttpStatus.OK);
        FlaglistListModel flList = response.getBody().getContent();
        assertNotNull(flList);
        assert(flList.getFlaglistEntries().get(0).getCommonName().equals("example.com"));

    }
    
    @Test
    public void blacklistControllerCreate() {
        SetupAuthentication();
        FlaglistInputModel flEntry = new FlaglistInputModel();
        flEntry.setCommonName("myorganizationllc.com");
        flEntry.setOrganizationName("My organization LLC");
        flEntry.setReason("This is not a real company");
        
        when(flaglistService.findByCommonName(anyString(), anyInt(), anyInt())).thenReturn(null);
        when(flaglistService.save(any())).thenReturn(true);
        ResponseEntity<IdModel> response = flController.createFlaglistEntry(flEntry);
        assert(response.getStatusCode() == HttpStatus.CREATED);
        assertNotNull(response.getBody());
        assert(response.getBody().toJson().length() > 0);
    }
    
    @Test
    public void blacklistControllerCreateFailure() {
        SetupAuthentication();
        FlaglistInputModel flEntry = new FlaglistInputModel();
        flEntry.setCommonName("myorganizationllc.com");
        flEntry.setOrganizationName("My organization LLC");
        flEntry.setReason("This is not a real company");
        
        when(flaglistService.findByCommonName(anyString(), anyInt(), anyInt())).thenReturn(null);
        when(flaglistService.save(any())).thenReturn(false);
        ResponseEntity<IdModel> response = flController.createFlaglistEntry(flEntry);
        assert(response.getStatusCode() == HttpStatus.BAD_REQUEST);
    }

    
    @Test
    public void blacklistControllerCreateFailure2() {
        SetupAuthentication();
        FlaglistInputModel blEntry = new FlaglistInputModel();
        blEntry.setCommonName("");
        blEntry.setOrganizationName("");
        blEntry.setReason("");
        
        when(flaglistService.findByCommonName(anyString(), anyInt(), anyInt())).thenReturn(null);
        when(flaglistService.save(any())).thenReturn(false);
        ResponseEntity<IdModel> response = flController.createFlaglistEntry(blEntry);
        assert(response.getStatusCode() == HttpStatus.BAD_REQUEST);
    }
    
    @Test
    public void blacklistControllerDelete() {
        SetupAuthentication();
        FlaglistModel value = new FlaglistModel(UUID.randomUUID(), "OrgName", "commonName", "serialNumber", "reason", "My Cool CA", "source", 1);
        when(flaglistService.findById(anyString())).thenReturn(value);
        when(flaglistService.delete(anyString())).thenReturn(true);
        ResponseEntity<HttpStatus> response = flController.deleteFlaglist("SomeIdString");
        assert(response.getStatusCode() == HttpStatus.OK);
    }

    @Test
    public void blacklistControllerDeleteFailure() {
        SetupAuthentication();
        FlaglistModel value = new FlaglistModel(UUID.randomUUID(), "OrgName", "commonName", "serialNumber", "reason", "insertedBy", "source", 1);
        when(flaglistService.findById(anyString())).thenReturn(value);
        when(flaglistService.delete(anyString())).thenReturn(false);
        ResponseEntity<HttpStatus> response = flController.deleteFlaglist("SomeIdString");
        assert(response.getStatusCode() == HttpStatus.BAD_REQUEST);
    }

    @Test
    public void blacklistControllerDeleteFailure2() {
        SetupAuthentication();
        when(flaglistService.findById(anyString())).thenReturn(null);
        ResponseEntity<HttpStatus> response = flController.deleteFlaglist("SomeIdString");
        assert(response.getStatusCode() == HttpStatus.BAD_REQUEST);
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