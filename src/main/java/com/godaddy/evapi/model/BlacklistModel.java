package com.godaddy.evapi.model;

import java.util.UUID;

public class BlacklistModel extends BaseModel {
    private UUID id;
    private String organizationName;
    private String commonName;
    private String serialNumber;
    
    public BlacklistModel() {
    }
    
    public BlacklistModel(UUID id, String organizationName, String commonName, String serialNumber) {
        this.id = id;
        setVars(organizationName, commonName, serialNumber);
    }
    
    public BlacklistModel(String organizationName, String commonName, String serialNumber) {
        id = UUID.randomUUID();
        setVars(organizationName, commonName, serialNumber);
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

    // Private functions / Helper functions
    
    private void setVars(String orgName, String commName, String serialNum) {
        organizationName = orgName;
        commonName = commName;
        serialNumber = serialNum;
    }
}
