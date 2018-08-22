package com.godaddy.evapi.model;

import java.util.List;

public class OrganizationListModel extends BaseListModel {
    private List<OrganizationModel> organizations;
    
    public List<OrganizationModel> getOrganizations() {
        return organizations;
    }
    
    public void setOrganizations(List<OrganizationModel> organizations) {
        this.organizations = organizations;
    }
    
}
