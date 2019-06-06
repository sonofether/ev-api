package com.godaddy.evapi.model;

import java.util.Date;
import java.util.UUID;

import org.junit.Test;

import io.jsonwebtoken.lang.Assert;

public class TopSiteModelTest {
    @Test
    public void TopSiteModelTest() {
        TopSiteModel model = new TopSiteModel();
        Assert.notNull(model);
        
        UUID uid = UUID.randomUUID();
        String url = "google.com";
        Date date = new Date();
        model = new TopSiteModel(uid, url, date);
        Assert.notNull(model);
        Assert.isTrue(model.getUrl().equals(url));
        Assert.isTrue(model.getId().equals(uid));
        Assert.isTrue(model.getLastUpdated().equals(date));
        
        model = new TopSiteModel(url);
        Assert.notNull(model);
        Assert.isTrue(model.getUrl().equals(url));
    }
}
