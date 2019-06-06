package com.godaddy.evapi.model;

import java.util.Date;
import java.util.UUID;

public class TopSiteModel {
    private UUID id;
    private String url;
    private Date lastUpdated;
    
    public TopSiteModel() {
    }
    
    public TopSiteModel(String url) {
        id = UUID.randomUUID();
        this.url = url;
        lastUpdated = new Date();
    }
    
    public TopSiteModel(UUID id, String url, Date date) {
        this.id = id;
        this.url = url;
        lastUpdated = date;
    }

    
    public UUID getId() {
        return id;
    }

    
    public void setId(UUID id) {
        this.id = id;
    }

    
    public String getUrl() {
        return url;
    }

    
    public void setUrl(String url) {
        this.url = url;
    }

    
    public Date getLastUpdated() {
        return lastUpdated;
    }

    
    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

}
