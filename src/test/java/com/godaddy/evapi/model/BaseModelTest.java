package com.godaddy.evapi.model;

import org.junit.Test;

public class BaseModelTest {
    @Test
    public void baseTest() {
        BaseModel model = new BaseModel();
        try {
            /*
            assert(model.toString().length() > 0);
            //assert(model.toObject("id:1") != null);
            assert(model.toJson().length() > 0);
            assert(model.toXml().length() > 0);
            */
        } catch (Exception ex) {
            ex.printStackTrace();
            assert(false);
        }
    }
}
