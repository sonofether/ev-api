package com.godaddy.evapi.model;

import java.util.UUID;

import org.junit.Test;

public class BlacklistModelTest {

    @Test
    public void BlacklistTest() {
        BlacklistModel model = new BlacklistModel("My Organization LLC", "example.com", "1234", "Failure to comply", "Test CA");
        model.setReason("Failure to comply twice");
        model.setInsertedBy("Test CA 2");
        assert(model.getId().toString().length() > 0);
        assert(model.getOrganizationName().equals("My Organization LLC"));
        assert(model.getSerialNumber().equals("1234"));
        assert(model.getReason().equals("Failure to comply twice"));
    }
}
