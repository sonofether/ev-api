package com.godaddy.evapi.model;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

public class FlaglistModel {
    private UUID id;
    private String organizationName;
    private String commonName;
    private String serialNumber;
    private String reason;
    private String insertedBy;
    private String source;
    private int severity;
    private Date lastUpdated;
    
    public FlaglistModel() {
    }
    
    public FlaglistModel(UUID id, String organizationName, String commonName, String serialNumber, String reason, String insertedBy, String source, int severity) {
        this.id = id;
        setVars(organizationName, commonName, serialNumber, reason, insertedBy, source, severity);
    }
    
    public FlaglistModel(String organizationName, String commonName, String serialNumber, String reason, String instertedBy, String source, int severity) {
        id = UUID.randomUUID();
        setVars(organizationName, commonName, serialNumber, reason, insertedBy, source, severity);
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
    
    
    public String getSource() {
        return source;
    }

    
    public void setSource(String source) {
        this.source = source;
    }

    
    public int getSeverity() {
        return severity;
    }

    
    public void setSeverity(int severity) {
        this.severity = severity;
    }

    
    public Date getLastUpdated() {
        return lastUpdated;
    }

    
    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    
    // Private functions / Helper functions
    
    private void setVars(String orgName, String commName, String serialNum, String reason, String insertedBy, String source, int severity) {
        organizationName = orgName;
        commonName = commName;
        serialNumber = serialNum;
        this.reason = reason;
        this.insertedBy = insertedBy;
        this.source = source;
        this.severity = severity;
        lastUpdated = new Date();
    }
}
