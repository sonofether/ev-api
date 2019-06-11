package com.godaddy.evapi.model;

import java.util.Date;
import java.util.UUID;

public class FraudModel extends BaseModel {
    private UUID id;
    String keyword;
    Date lastUpdated;
    int fraudType;
    String description;
    String insertedBy;
    
    public FraudModel(String keyword, int type, String description, String insertedBy) {
        this.setup(UUID.randomUUID(), keyword, new Date(), type, description, insertedBy);
    }
    
    public FraudModel(UUID id, String keyword, Date lastUpdated, int type, String description, String insertedBy) {
        this.setup(id, keyword, lastUpdated, type, description, insertedBy);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    public Date getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public int getFraudType() {
        return fraudType;
    }
    
    public void setFraudType(int type) {
        this.fraudType = type;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getInsertedBy() {
        return insertedBy;
    }
    
    public void setInsertedBy(String insertedBy) {
        this.insertedBy = insertedBy;
    }

    
    // PRIVATE METHODS 
    
    private void setup(UUID id, String keyword, Date lastUpdated, int type, String description, String insertedBy) {
        this.id = id;
        this.keyword = keyword;
        this.lastUpdated = lastUpdated;
        this.fraudType = type;
        this.description = description;
        this.insertedBy = insertedBy;
    }

}
