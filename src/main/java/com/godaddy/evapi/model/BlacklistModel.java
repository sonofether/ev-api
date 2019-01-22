package com.godaddy.evapi.model;

import java.util.UUID;

public class BlacklistModel extends BaseModel {
    private UUID id;
    private String organizationName;
    private String commonName;
    private String serialNumber;
    private String reason;
    private String insertedBy;
    
    public BlacklistModel() {
    }
    
    public BlacklistModel(UUID id, String organizationName, String commonName, String serialNumber, String reason, String insertedBy) {
        this.id = id;
        setVars(organizationName, commonName, serialNumber, reason, insertedBy);
    }
    
    public BlacklistModel(String organizationName, String commonName, String serialNumber, String reason, String instertedBy) {
        id = UUID.randomUUID();
        setVars(organizationName, commonName, serialNumber, reason, insertedBy);
    }
    
    // Getters and Setters
    
    public UUID getId() {
        return id;
    }

    
    public void setId(UUID id) {
        this.id = id;
    }

    
    public String getOrganizationName() {
        return organizationName;
    }

    
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    
    public String getCommonName() {
        return commonName;
    }

    
    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    
    public String getSerialNumber() {
        return serialNumber;
    }

    
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public String getReason() {
        return reason;
    }

    
    public void setReason(String reason) {
        this.reason = reason;
    }

    
    public String getInsertedBy() {
        return insertedBy;
    }

    
    public void setInsertedBy(String insertedBy) {
        this.insertedBy = insertedBy;
    }

    // Private functions / Helper functions
        
    private void setVars(String orgName, String commName, String serialNum, String reason, String insertedBy) {
        organizationName = orgName;
        commonName = commName;
        serialNumber = serialNum;
        this.reason = reason;
        this.insertedBy = insertedBy;
    }
}
