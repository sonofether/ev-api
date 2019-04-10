package com.godaddy.evapi.controller;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.godaddy.evapi.model.BlacklistDTOModel;
import com.godaddy.evapi.model.BlacklistListModel;
import com.godaddy.evapi.model.BlacklistModel;
import com.godaddy.evapi.service.IBlacklistService;
import com.godaddy.evapi.service.TestBlacklistService;

public class CNameControllerTest {
    @Mock
    IBlacklistService blacklistService;
    
    @InjectMocks
    private CNameController cNameController;
    
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void cNameControllerGetTest() {
        when(blacklistService.findById(anyString())).thenReturn(TestBlacklistService.generateBlacklist());
        ResponseEntity<BlacklistModel> response = cNameController.getById("1234");
        assert(response.getStatusCode() == HttpStatus.OK);
        BlacklistModel bl = response.getBody();
        assertNotNull(bl);
        assert(bl.getCommonName().equals("example.com"));
    }
    
    @Test
    public void cNameControllerGetFailureTest() {
        when(blacklistService.findById(anyString())).thenReturn(null);
        ResponseEntity<BlacklistModel> response = cNameController.getById("1234");
        assert(response.getStatusCode() == HttpStatus.NOT_FOUND);
    }

    @Test
    public void cNameControllerCheckBlacklistTest() {
        when(blacklistService.findByCommonName(anyString(), anyInt(), anyInt())).thenReturn(TestBlacklistService.generateBlacklistList());
        BlacklistDTOModel response = cNameController.getBlacklistByCName("example.com");
        assertNotNull(response);
        assert(response.isBlacklisted());
    }
    
    @Test
    public void cNameControllerCheckBlacklistFailureTest() {
        when(blacklistService.findByCommonName(anyString(), anyInt(), anyInt())).thenReturn(null);
        BlacklistDTOModel response = cNameController.getBlacklistByCName("example.com");
        assertNotNull(response);
        assert(response.isBlacklisted() == false);
    }
    
    @Test
    public void cNameControllerCheckBlacklistFailureTest2() {
        when(blacklistService.findByCommonName(anyString(), anyInt(), anyInt())).thenReturn(new BlacklistListModel());
        BlacklistDTOModel response = cNameController.getBlacklistByCName("example.com");
        assertNotNull(response);
        assert(response.isBlacklisted() == false);
    }

    @Test
    public void testDelete() {
        ResponseEntity<HttpStatus> response = cNameController.delete("");
        assert(response.getStatusCode() == HttpStatus.NOT_IMPLEMENTED);
    }
    
    @Test
    public void testPut() {
        ResponseEntity<HttpStatus> response = cNameController.update("");
        assert(response.getStatusCode() == HttpStatus.NOT_IMPLEMENTED);
    }
    
    @Test
    public void testPost() {
        ResponseEntity<HttpStatus> response = cNameController.createEntry();
        assert(response.getStatusCode() == HttpStatus.NOT_IMPLEMENTED);
    }
    
    @Test
    public void testGetAll() {
        ResponseEntity<HttpStatus> response = cNameController.getAll();
        assert(response.getStatusCode() == HttpStatus.NOT_IMPLEMENTED);
    }
}
