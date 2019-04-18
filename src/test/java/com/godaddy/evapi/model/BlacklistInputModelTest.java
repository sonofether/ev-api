package com.godaddy.evapi.model;

import org.junit.Test;

public class BlacklistInputModelTest {

    @Test
    public void BlacklistInputModelTest() {
        BlacklistInputModel model = new BlacklistInputModel();
        model.setSerialNumber("123456");
        assert(model.getSerialNumber().equals("123456"));
    }
}
