package com.godaddy.evapi.model;

import java.util.Date;
import java.util.UUID;

import org.junit.Test;

public class CertificateModelTest {

    @Test
    public void certificateTest() {
        UUID orgId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        Date date = new Date();
        CertificateModel model = new CertificateModel();
        model = new CertificateModel( id, "org name", "ca name", "common name", date, date, 0, "valdiation level" );
        model = new CertificateModel( orgId, id, "org name", "ca name", "common name", date, date, 0, "valdiation level" );
        
        model.setCa("ca");
        model.setCommonName("example.com");
        model.setExpiresDate(date);
        model.setId(id);
        model.setIssuedDate(date);
        model.setOrganizationId(orgId);
        model.setOrganizationName("organization llc");
        model.setStatus(1);
        model.setValidationLevel("validation");
        
        assert(model.getCa().equals("ca"));
        assert(model.getCommonName().equals("example.com"));
        assert(model.getExpiresDate() == date);
        assert(model.getId() == id);
        assert(model.getIssuedDate() == date);
        assert(model.getOrganizationId() == orgId);
        assert(model.getOrganizationName().equals("organization llc"));
        assert(model.getStatus() == 1);
        assert(model.getValidationLevel().equals("validation"));
        
    }
}
