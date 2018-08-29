package com.godaddy.evapi.model;

import java.util.UUID;

public class ValidationInputModel extends BaseModel {
    private UUID certId;
    private String item;
    private String validates;
    
    public UUID getCertId() {
        return certId;
    }
    
    public void setCertId(UUID certId) {
        this.certId = certId;
    }
    
    public String getItem() {
        return item;
    }
    
    public void setItem(String item) {
        this.item = item;
    }
    
    public String getValidates() {
        return validates;
    }
    
    public void setValidates(String validates) {
        this.validates = validates;
    }
    
    
}
