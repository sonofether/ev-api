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
    public void cNameControllerCheckBlacklistTest() {
        when(blacklistService.findByCommonName(anyString(), anyInt(), anyInt())).thenReturn(TestBlacklistService.generateBlacklistList());
        BlacklistDTOModel response = cNameController.getBlacklistByCName("example.com");
        assertNotNull(response);
        assert(response.isBlacklisted());
    }
}
