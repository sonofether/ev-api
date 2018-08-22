package com.godaddy.evapi.model;

import java.util.UUID;

public class ValidationInputModel extends BaseModel {
    private UUID certId;
    private String validationItem;
    private String validates;
    
    public UUID getCertId() {
        return certId;
    }
    
    public void setCertId(UUID certId) {
        this.certId = certId;
    }
    
    public String getValidationItem() {
        return validationItem;
    }
    
    public void setValidationItem(String validationItem) {
        this.validationItem = validationItem;
    }
    
    public String getValidates() {
        return validates;
    }
    
    public void setValidates(String validates) {
        this.validates = validates;
    }
    
    
}
