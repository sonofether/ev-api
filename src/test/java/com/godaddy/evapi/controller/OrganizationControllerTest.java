package com.godaddy.evapi.controller;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.godaddy.evapi.model.OrganizationListModel;
import com.godaddy.evapi.model.OrganizationModel;
import com.godaddy.evapi.service.IOrganizationService;
import com.godaddy.evapi.service.TestOrganizationService;

public class OrganizationControllerTest {
    @Mock
    IOrganizationService organizationService;
    
    @InjectMocks
    private OrganizationController orgController;
    
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
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
    public void organizationControllerGetAllTest() {
        Optional<Integer> optInt = Optional.empty();
        when(organizationService.findAll(anyInt(), anyInt())).thenReturn(TestOrganizationService.generateOrganizationList());
        ResponseEntity<OrganizationListModel> response = orgController.GetOrganizationList(optInt, optInt);
        assert(response.getStatusCode() == HttpStatus.OK);
        OrganizationListModel orgList = response.getBody();
        assertNotNull(orgList);
        assert(orgList.getOrganizations().get(0).getCommonName().equals("example.com"));
    }
    
    @Test
    public void organizationControllerGetBySerialNumberTest() {
        Optional<Integer> optInt = Optional.empty();
        when(organizationService.findBySerialNumber(anyString(), anyInt(), anyInt())).thenReturn(TestOrganizationService.generateOrganizationList());
        ResponseEntity<OrganizationListModel> response = orgController.GetOrganizationBySerialNumber("1234", optInt, optInt);
        assert(response.getStatusCode() == HttpStatus.OK);
        OrganizationListModel orgList = response.getBody();
        assertNotNull(orgList);
        assert(orgList.getOrganizations().get(0).getCommonName().equals("example.com"));
    }
    
    @Test
    public void organizationControllerGetByCommonNameTest() {
        Optional<Integer> optInt = Optional.empty();
        when(organizationService.findByCommonName(anyString(), anyInt(), anyInt())).thenReturn(TestOrganizationService.generateOrganizationList());
        ResponseEntity<OrganizationListModel> response = orgController.GetOrganizationByCommonName("example.com", optInt, optInt);
        assert(response.getStatusCode() == HttpStatus.OK);
        OrganizationListModel orgList = response.getBody();
        assertNotNull(orgList);
        assert(orgList.getOrganizations().get(0).getCommonName().equals("example.com"));
    }
    
    @Test
    public void organizationControllerGetByOrganizationNameTest() {
        Optional<Integer> optInt = Optional.empty();
        when(organizationService.findByOrganizationName(anyString(), anyInt(), anyInt())).thenReturn(TestOrganizationService.generateOrganizationList());
        ResponseEntity<OrganizationListModel> response = orgController.GetOrganizationByName("Asink, Inc", optInt, optInt);
        assert(response.getStatusCode() == HttpStatus.OK);
        OrganizationListModel orgList = response.getBody();
        assertNotNull(orgList);
        assert(orgList.getOrganizations().get(0).getCommonName().equals("example.com"));
    }
    
    @Test
    public void organizationControllerGetByNameSerialNumberCountry() {
        Optional<Integer> optInt = Optional.empty();
        when(organizationService.findByNameSerialNumberCountry(anyString(), anyString(), anyString(), anyInt(), anyInt())).thenReturn(TestOrganizationService.generateOrganizationList());
        ResponseEntity<OrganizationListModel> response = orgController.GetOrganizationByNameSerialNumberCountry("example.com", "1234", "US", optInt, optInt);
        assert(response.getStatusCode() == HttpStatus.OK);
        OrganizationListModel orgList = response.getBody();
        assertNotNull(orgList);
        assert(orgList.getOrganizations().get(0).getCommonName().equals("example.com"));
    }

    @Test
    public void organizationControllerGetByNameSerialNumberCountryState() {
        Optional<Integer> optInt = Optional.empty();
        when(organizationService.findByNameSerialNumberCountryState(anyString(), anyString(), anyString(), anyString(), anyInt(), anyInt())).thenReturn(TestOrganizationService.generateOrganizationList());
        ResponseEntity<OrganizationListModel> response = orgController.GetOrganizationByNameSerialNumberCountryState("example.com", "1234", "US", "AZ", optInt, optInt);
        assert(response.getStatusCode() == HttpStatus.OK);
        OrganizationListModel orgList = response.getBody();
        assertNotNull(orgList);
        assert(orgList.getOrganizations().get(0).getCommonName().equals("example.com"));
    }

}