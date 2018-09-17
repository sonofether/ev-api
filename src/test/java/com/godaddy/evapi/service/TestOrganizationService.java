package com.godaddy.evapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.elasticsearch.client.transport.TransportClient;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.godaddy.evapi.controller.OrganizationController;
import com.godaddy.evapi.model.OrganizationListModel;
import com.godaddy.evapi.model.OrganizationModel;

public class TestOrganizationService implements IOrganizationService {

    @Override
    public OrganizationModel save(OrganizationModel org) {
        OrganizationModel orgModel = new OrganizationModel();
        orgModel.setCa(org.getCa());
        orgModel.setCommonName(org.getCommonName());
        orgModel.setCountryName(org.getCountryName());
        orgModel.setLocalityName(org.getLocalityName());
        orgModel.setOrganizationName(org.getOrganizationName());
        orgModel.setSerialNumber(org.getSerialNumber());
        orgModel.setStateOrProvinceName(org.getStateOrProvinceName());
        orgModel.setId(org.getId());
        return orgModel;
    }

    @Override
    public boolean delete(String id) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public OrganizationModel findById(String id) {
        // TODO Auto-generated method stub
        return generateOrganization();
    }

    @Override
    public OrganizationListModel findAll(int offset, int limit) {
        return generateOrganizationList();
    }

    @Override
    public OrganizationListModel findByCommonName(String commonName, int offset, int limit) {
        return generateOrganizationList();
    }
    
    @Override
    public OrganizationListModel findByOrganizationName(String orgName, int offset, int limit) {
        return generateOrganizationList();
    }

    @Override
    public OrganizationListModel findBySerialNumber(String serialNumber, int offset, int limit) {
        return generateOrganizationList();
    }

    @Override
    public OrganizationListModel findByNameSerialNumberCountry(String name, String serialNumber, String country, int offset, int limit) {
        return generateOrganizationList();
    }

    @Override
    public OrganizationListModel findByNameSerialNumberCountryState(String name, String serialNumber, String country, String state, int offset, int limit) {
        return generateOrganizationList();
    }

    public static OrganizationListModel generateOrganizationList() {
        OrganizationListModel orgList = new OrganizationListModel();
        orgList.setCount(1);
        orgList.setOffset(0);
        orgList.setLimit(25);
        List<OrganizationModel> organizations = new ArrayList<OrganizationModel>();
        organizations.add(generateOrganization());
        orgList.setOrganizations(organizations);
        return orgList;
    }
    
    public static OrganizationModel generateOrganization() {
        OrganizationModel orgModel = new OrganizationModel();

        orgModel.setCa("Cert Authority");
        orgModel.setCommonName("example.com");
        orgModel.setCountryName("US");
        orgModel.setId(UUID.randomUUID());
        orgModel.setLocalityName("Scottsdale");
        orgModel.setOrganizationName("My Org");
        orgModel.setSerialNumber("123456");
        orgModel.setStateOrProvinceName("AZ");
        
        return orgModel;
    }
}
