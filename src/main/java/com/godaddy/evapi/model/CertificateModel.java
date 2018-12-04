package com.godaddy.evapi.model;

import java.util.Date;
import java.util.UUID;

public class CertificateModel extends BaseModel {
    private UUID id;
    private String organizationName;
    private String ca;
    private UUID organizationId;
    private String commonName;
    private Date expiresDate;
    private Date issuedDate;
    private int status;
    private String validationLevel;
  
    public CertificateModel() {
        // Default constructor. Do nothing.
    }
  
    public CertificateModel(UUID id, UUID organizationId, String organizationName, String ca, String commonName, Date expiresDate, Date issuedDate, int status, String validationLevel) {
        this.id = id;
        setupRecord(organizationId, organizationName, ca, commonName, expiresDate, issuedDate, status, validationLevel);
    }
  
    public CertificateModel(UUID organizationId, String organizationName, String ca, String commonName, Date expiresDate, Date issuedDate, int status, String validationLevel) {
        this.id = UUID.randomUUID();
        setupRecord(organizationId, organizationName, ca, commonName, expiresDate, issuedDate, status, validationLevel);
    }
  
    
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
    
    public String getCa() {
        return ca;
    }
    
    
    public void setCa(String ca) {
        this.ca = ca;
    }
 
    public UUID getOrganizationId() {
        return organizationId;
    }
    
    
    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }
    
    
    public String getCommonName() {
        return commonName;
    }
    
    
    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }
    
    
    public Date getExpiresDate() {
        return expiresDate;
    }
    
    
    public void setExpiresDate(Date expiresDate) {
        this.expiresDate = expiresDate;
    }
    
    
    public Date getIssuedDate() {
        return issuedDate;
    }
    
    
    public void setIssuedDate(Date issuedDate) {
        this.issuedDate = issuedDate;
    }
    
    
    public int getStatus() {
        return status;
    }
    
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    
    public String getValidationLevel() {
        return validationLevel;
    }
    
    
    public void setValidationLevel(String validationLevel) {
        this.validationLevel = validationLevel;
    }

    
    //PRIVATE FUNCTIONS / HELPERS
    private void setupRecord(UUID organizationId, String organizationName, String ca, String commonName, Date expiresDate, Date issuedDate, int status, String validationLevel) {
        this.organizationId = organizationId;
        this.organizationName = organizationName;
        this.ca = ca;
        this.commonName = commonName;
        this.expiresDate = expiresDate;
        this.issuedDate = issuedDate;
        this.status = status;
        this.validationLevel = validationLevel;
    }

}
