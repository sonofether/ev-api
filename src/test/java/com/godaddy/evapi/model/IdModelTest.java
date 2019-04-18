package com.godaddy.evapi.model;

import org.junit.Test;

public class IdModelTest {

    @Test
    public void IdModelTest() {
        IdModel model = new IdModel("1234");
        assert(model.getId().equals("1234"));
        model.setId("5678");
        assert(model.getId().equals("5678"));
    }
}
