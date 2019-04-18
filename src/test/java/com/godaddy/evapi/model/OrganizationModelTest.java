package com.godaddy.evapi.model;

import java.util.UUID;

import org.junit.Test;

public class OrganizationModelTest {
    @Test
    public void organizationTest() {
        UUID id = UUID.randomUUID();
        OrganizationModel model = new OrganizationModel();
        model = new OrganizationModel("organization llc", "example.org", "serial", "locality", "state", "country", "ca", "phone", "address");
        model = new OrganizationModel(id, "organization llc", "example.org", "serial", "locality1", "state", "country", "ca", "phone1", "address1");

        model.setCommonName("example.com");
        model.setAddress("address");
        model.setCountryCode("country");
        model.setLocalityName("locality");
        model.setPhoneNumber("phone");
        
        assert(model.getAddress().equals("address"));
        assert(model.getCa().equals("ca"));
        assert(model.getCommonName().equals("example.com"));
        assert(model.getCountryCode().equals("country"));
        assert(model.getCountryName().equals("country"));
        assert(model.getId() == id);
        assert(model.getLocalityName().equals("locality"));
        assert(model.getOrganizationName().equals("organization llc"));
        assert(model.getPhoneNumber().equals("phone"));
        assert(model.getSerialNumber().equals("serial"));
        assert(model.getStateOrProvinceName().equals("state"));
    }
}
