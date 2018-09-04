package com.godaddy.evapi.model;


public class BlacklistModel extends BaseModel {
    private boolean blacklisted;
    
    public boolean isBlacklisted() {
        return blacklisted;
    }
    
    public void setBlacklisted(boolean blacklisted) {
        this.blacklisted = blacklisted;
    }
    
}
