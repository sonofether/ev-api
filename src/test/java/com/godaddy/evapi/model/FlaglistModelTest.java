package com.godaddy.evapi.model;

import java.util.UUID;

import org.junit.Test;

public class FlaglistModelTest {
    @Test
    public void flaglistTest() {
        FlaglistModel model = new FlaglistModel("Test Organization LLC", "example.com", "1234", "Failure to comply", "Test CA", "ca", 1);
        
        model.setCommonName("common");
        model.setId(UUID.randomUUID());
        model.setInsertedBy("inserted");
        model.setLastUpdated(null);
        model.setOrganizationName("org");
        model.setReason("reason");
        model.setSerialNumber("serial");
        model.setSeverity(0);
        model.setSource("source");
        
        assert(model.getCommonName().equals("common"));
        assert(model.getId().toString().length() > 0);
        assert(model.getInsertedBy().equals("inserted"));
        assert(model.getLastUpdated() == null);
        assert(model.getOrganizationName().equals("org"));
        assert(model.getReason().equals("reason"));
        assert(model.getSerialNumber().equals("serial"));
        assert(model.getSeverity() == 0);
        assert(model.getSource().equals("source"));
        
    }
}