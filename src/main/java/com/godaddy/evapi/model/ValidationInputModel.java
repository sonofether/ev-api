package com.godaddy.evapi.model;

import java.util.UUID;

public class ValidationInputModel extends BaseModel {
    private UUID certId;
    private String validates;
    
    public UUID getCertId() {
        return certId;
    }
    
    public void setCertId(UUID certId) {
        this.certId = certId;
    }
      
    public String getValidates() {
        return validates;
    }
    
    public void setValidates(String validates) {
        this.validates = validates;
    }
    
    
}
