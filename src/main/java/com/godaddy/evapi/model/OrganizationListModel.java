package com.godaddy.evapi.model;

import java.util.ArrayList;
import java.util.List;

public class OrganizationListModel extends BaseListModel {
    private List<OrganizationModel> organizations;
    
    public OrganizationListModel() {
        organizations = new ArrayList<OrganizationModel>();
    }
    
    public List<OrganizationModel> getOrganizations() {
        return organizations;
    }
    
    public void setOrganizations(List<OrganizationModel> organizations) {
        this.organizations = organizations;
    }
    
}
