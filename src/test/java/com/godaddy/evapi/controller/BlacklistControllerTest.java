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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.godaddy.evapi.model.BlacklistInputModel;
import com.godaddy.evapi.model.BlacklistListModel;
import com.godaddy.evapi.model.BlacklistModel;
import com.godaddy.evapi.model.IdModel;
import com.godaddy.evapi.service.IBlacklistService;
import com.godaddy.evapi.service.TestBlacklistService;

import io.jsonwebtoken.Claims;

public class BlacklistControllerTest {
    @Mock
    IBlacklistService blacklistService;
    
    @InjectMocks
    private BlacklistController blController;
    
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void blacklistControllerGetByIdTest() {
        when(blacklistService.findById(anyString())).thenReturn(TestBlacklistService.generateBlacklist());
        ResponseEntity<BlacklistModel> response = blController.getById("1234");
        assert(response.getStatusCode() == HttpStatus.OK);
        BlacklistModel bl = response.getBody();
        assertNotNull(bl);
        assert(bl.getCommonName().equals("example.com"));
    }
    
    @Test
    public void blacklistControllerGetAll() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("1.2.3.4");
        request.setServerName("www.example.com");
        request.setRequestURI("/");
        request.setQueryString("");
        Optional<Integer> optInt = Optional.empty();
        when(blacklistService.findAll(anyInt(), anyInt())).thenReturn(TestBlacklistService.generateBlacklistList());
        ResponseEntity<Resource<BlacklistListModel>> response = blController.getAll(request, optInt, optInt);
        assert(response.getStatusCode() == HttpStatus.OK);
        BlacklistListModel blList = response.getBody().getContent();
        assertNotNull(blList);
        assert(blList.getBlacklistEntries().get(0).getCommonName().equals("example.com"));
    }

    @Test
    public void blacklistControllerGetByCA() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("1.2.3.4");
        request.setServerName("www.example.com");
        request.setRequestURI("/");
        request.setQueryString("");
        Optional<Integer> optInt = Optional.empty();
        when(blacklistService.findByCA(anyString(), anyInt(), anyInt())).thenReturn(TestBlacklistService.generateBlacklistList());
        ResponseEntity<Resource<BlacklistListModel>> response = blController.getBlacklistByCA(request, optInt, optInt, "My CA");
        assert(response.getStatusCode() == HttpStatus.OK);
        BlacklistListModel blList = response.getBody().getContent();
        assertNotNull(blList);
        assert(blList.getBlacklistEntries().get(0).getCommonName().equals("example.com"));

    }
    
    @Test
    public void blacklistControllerGetByCName() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("1.2.3.4");
        request.setServerName("www.example.com");
        request.setRequestURI("/");
        request.setQueryString("");
        Optional<Integer> optInt = Optional.empty();
        when(blacklistService.findByCommonName(anyString(), anyInt(), anyInt())).thenReturn(TestBlacklistService.generateBlacklistList());
        ResponseEntity<Resource<BlacklistListModel>> response = blController.getBlacklistByCName(request, optInt, optInt, "example.com");
        assert(response.getStatusCode() == HttpStatus.OK);
        BlacklistListModel blList = response.getBody().getContent();
        assertNotNull(blList);
        assert(blList.getBlacklistEntries().get(0).getCommonName().equals("example.com"));

    }
    
    @Test
    public void blacklistControllerCreate() {
        SetupAuthentication();
        BlacklistInputModel blEntry = new BlacklistInputModel();
        blEntry.setCommonName("myorganizationllc.com");
        blEntry.setOrganizationName("My organization LLC");
        blEntry.setReason("This is not a real company");
        
        when(blacklistService.findByCommonName(anyString(), anyInt(), anyInt())).thenReturn(null);
        when(blacklistService.save(any())).thenReturn(true);
        ResponseEntity<IdModel> response = blController.createBlacklistEntry(blEntry);
        assert(response.getStatusCode() == HttpStatus.OK);
        assertNotNull(response.getBody());
        assert(response.getBody().toJson().length() > 0);
    }
    
    @Test
    public void blacklistControllerCreateFailure() {
        SetupAuthentication();
        BlacklistInputModel blEntry = new BlacklistInputModel();
        blEntry.setCommonName("myorganizationllc.com");
        blEntry.setOrganizationName("My organization LLC");
        blEntry.setReason("This is not a real company");
        
        when(blacklistService.findByCommonName(anyString(), anyInt(), anyInt())).thenReturn(null);
        when(blacklistService.save(any())).thenReturn(false);
        ResponseEntity<IdModel> response = blController.createBlacklistEntry(blEntry);
        assert(response.getStatusCode() == HttpStatus.BAD_REQUEST);
    }

    
    @Test
    public void blacklistControllerCreateFailure2() {
        SetupAuthentication();
        BlacklistInputModel blEntry = new BlacklistInputModel();
        blEntry.setCommonName("");
        blEntry.setOrganizationName("");
        blEntry.setReason("");
        
        when(blacklistService.findByCommonName(anyString(), anyInt(), anyInt())).thenReturn(null);
        when(blacklistService.save(any())).thenReturn(false);
        ResponseEntity<IdModel> response = blController.createBlacklistEntry(blEntry);
        assert(response.getStatusCode() == HttpStatus.BAD_REQUEST);
    }
    
    @Test
    public void blacklistControllerDelete() {
        SetupAuthentication();
        BlacklistModel value = new BlacklistModel(UUID.randomUUID(), "OrgName", "commonName", "serialNumber", "reason", "insertedBy");
        when(blacklistService.findById(anyString())).thenReturn(value);
        when(blacklistService.delete(anyString())).thenReturn(true);
        ResponseEntity<HttpStatus> response = blController.deleteBlacklist("SomeIdString");
        assert(response.getStatusCode() == HttpStatus.OK);
    }

    @Test
    public void blacklistControllerDeleteFailure() {
        SetupAuthentication();
        BlacklistModel value = new BlacklistModel(UUID.randomUUID(), "OrgName", "commonName", "serialNumber", "reason", "insertedBy");
        when(blacklistService.findById(anyString())).thenReturn(value);
        when(blacklistService.delete(anyString())).thenReturn(false);
        ResponseEntity<HttpStatus> response = blController.deleteBlacklist("SomeIdString");
        assert(response.getStatusCode() == HttpStatus.BAD_REQUEST);
    }

    @Test
    public void blacklistControllerDeleteFailure2() {
        SetupAuthentication();
        when(blacklistService.findById(anyString())).thenReturn(null);
        ResponseEntity<HttpStatus> response = blController.deleteBlacklist("SomeIdString");
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
