package com.godaddy.evapi.model;

import java.util.Date;

import org.junit.Test;

public class OrganizationInputModelTest {

    @Test
    public void organizationTest() {
        Date date = new Date();
        OrganizationInputModel model = new OrganizationInputModel();
        model.setOrganizationName("organization");
        model.setCommonName("common");
        model.setIssuedDate(date);
        model.setExpirationDate(date);
        model.setSerialNumber("serial");
        model.setAddress("address");
        model.setCountryName("country");
        model.setLocalityName("locality");
        model.setStateOrProvinceName("state");
        model.setPhoneNumber("phone");
                
        assert(model.getAddress().equals("address"));
        assert(model.getCommonName().equals("common"));
        assert(model.getLocalityName().equals("locality"));
        assert(model.getOrganizationName().equals("organization"));
        assert(model.getCountryName().equals("country"));
        assert(model.getPhoneNumber().equals("phone"));
        assert(model.getSerialNumber().equals("serial"));
        assert(model.getStateOrProvinceName().equals("state"));

        assert(model.getExpirationDate().equals(date));
        assert(model.getIssuedDate().equals(date));
        
    }
}
