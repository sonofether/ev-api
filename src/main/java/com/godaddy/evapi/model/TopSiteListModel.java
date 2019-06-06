package com.godaddy.evapi.model;

import java.util.ArrayList;
import java.util.List;

public class TopSiteListModel extends BaseListModel {
    private List<TopSiteModel> topSites;
    
    public TopSiteListModel() {
        topSites = new ArrayList<TopSiteModel>();
    }
    
    public List<TopSiteModel> getTopSites() {
        return topSites;
    }
    
    public void setTopSites(List<TopSiteModel> topSites) {
        this.topSites = topSites;
    }
}
