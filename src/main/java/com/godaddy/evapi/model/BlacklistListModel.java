package com.godaddy.evapi.model;

import java.util.ArrayList;
import java.util.List;

public class BlacklistListModel extends BaseListModel {
    private List<BlacklistModel> blacklistEntries;
    
    public BlacklistListModel() {
        blacklistEntries = new ArrayList<BlacklistModel>();
    }
    
    public List<BlacklistModel> getBlacklistEntries() {
        return blacklistEntries;
    }
    
    public void setBlacklistEntries(List<BlacklistModel> blacklistEntries) {
        this.blacklistEntries = blacklistEntries;
    }
}
