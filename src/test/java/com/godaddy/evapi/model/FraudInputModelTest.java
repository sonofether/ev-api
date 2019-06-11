package com.godaddy.evapi.model;

import org.junit.Test;
import org.springframework.util.Assert;

public class FraudInputModelTest {

    @SuppressWarnings("deprecation")
    @Test
    public void fraudInputModelTest() {
        FraudInputModel model = new FraudInputModel();
        model.setDescription("description");
        model.setType(1);
        model.setKeyword("keyword");
        
        Assert.isTrue(model.getKeyword().equals("keyword"));
        Assert.isTrue(model.getDescription().equals("description"));
        Assert.isTrue(model.getType() == 1);
    }
}
