package com.godaddy.evapi.model;

import java.util.UUID;

public class ValidationItemModel extends BaseModel {
    private UUID id;
    private String itemUrl;
    private String fileName;
    private UUID certificateId;
    private String validates;
    private int status;
    
    public ValidationItemModel() {
        // Default constructor - Do nothing
    }
    
    public ValidationItemModel(UUID id, String url, String fileName, UUID certificateId, String validates, int status) {
        this.id = id;
        setValues(url, fileName, certificateId, validates, status);
    }
    
    public ValidationItemModel(String url, String fileName, UUID certificateId, String validates, int status) {
        this.id = UUID.randomUUID();
        setValues(url, fileName, certificateId, validates, status);
    }
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getItemUrl() {
        return itemUrl;
    }
    
    public void setItemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
    }

    public UUID getCertificateId() {
        return certificateId;
    }
    
    public void setCertificateId(UUID certificateId) {
        this.certificateId = certificateId;
    }
    
    public String getValidates() {
        return validates;
    }
    
    public void setValidates(String validates) {
        this.validates = validates;
    }

    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
    
    private void setValues(String url, String fileName, UUID certificateId, String validates, int status) {
        this.itemUrl = url;
        this.fileName = fileName;
        this.certificateId = certificateId;
        this.validates = validates;
        this.status = status;
    }
}
