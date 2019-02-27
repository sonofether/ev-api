package com.godaddy.evapi.model;

import java.util.ArrayList;
import java.util.List;

public class FlaglistListModel extends BaseListModel {
    private List<FlaglistModel> flaglistEntries;
    
    public FlaglistListModel() {
        flaglistEntries = new ArrayList<FlaglistModel>();
    }
    
    public List<FlaglistModel> getFlaglistEntries() {
        return flaglistEntries;
    }
    
    public void setFlaglistEntries(List<FlaglistModel> flaglistEntries) {
        this.flaglistEntries = flaglistEntries;
    }
}
