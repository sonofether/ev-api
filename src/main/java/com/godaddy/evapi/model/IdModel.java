package com.godaddy.evapi.model;


public class IdModel extends BaseModel {
    private String id;
    
    public IdModel(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
}
