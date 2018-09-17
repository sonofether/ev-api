package com.godaddy.evapi.model;


public class BlacklistDTOModel extends BaseModel {
    private boolean blacklisted;
    
    public BlacklistDTOModel() {
        blacklisted = false;
    }
    
    public BlacklistDTOModel(boolean blacklisted) {
        this.blacklisted = blacklisted;
    }
    
    public boolean isBlacklisted() {
        return blacklisted;
    }
    
    public void setBlacklisted(boolean blacklisted) {
        this.blacklisted = blacklisted;
    }
    
}
