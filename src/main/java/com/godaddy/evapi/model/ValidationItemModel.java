package com.godaddy.evapi.model;

import java.util.UUID;

public class ValidationItemModel extends BaseModel {
    private UUID id;
    private String itemUrl;
    
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
    
}
