package com.godaddy.evapi.model;

import java.util.Date;
import java.util.UUID;

import org.junit.Test;

import io.jsonwebtoken.lang.Assert;

public class FraudModelTest {

    @Test
    public void fraudModelInstantiateTest() {
        String keyword = "keyword";
        String description = "description";
        String insertedBy = "insertedBy";
        int type = 1;
        UUID id = UUID.randomUUID();
        Date date = new Date();
        
        FraudModel model = new FraudModel(keyword, type, description, insertedBy);
        FraudModel model2 = new FraudModel(id, "keyword", date, 1, "description", "insertedBy");
        
        Assert.isTrue(model.getDescription().equals(description));
        Assert.isTrue(model.getFraudType() == type);
        Assert.isTrue(model.getKeyword().equals(keyword));
        Assert.isTrue(model.getInsertedBy().equals(insertedBy));
        Assert.isTrue(model2.getId().equals(id));
        Assert.isTrue(model2.getLastUpdated().equals(date));

        String append = Integer.toString(type);
        model.setDescription(description + append);
        model.setKeyword(keyword + append);
        model.setFraudType(type + type);
        model.setId(id);
        model.setInsertedBy(insertedBy + append);
        model.setLastUpdated(date);
        
        Assert.isTrue(model.getDescription().equals(description + append));
        Assert.isTrue(model.getFraudType() == type + type);
        Assert.isTrue(model.getKeyword().equals(keyword + append));
        Assert.isTrue(model.getInsertedBy().equals(insertedBy + append));
        Assert.isTrue(model.getId().equals(id));
        Assert.isTrue(model.getLastUpdated().equals(date));
    }
}
