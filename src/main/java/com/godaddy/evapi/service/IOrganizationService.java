package com.godaddy.evapi.service;

import java.util.List;

import com.godaddy.evapi.model.OrganizationListModel;
import com.godaddy.evapi.model.OrganizationModel;

public interface IOrganizationService {

    // Create/Update
    OrganizationModel save(OrganizationModel org);
    // Delete
    void delete(OrganizationModel org);
    // Read/Get
    OrganizationModel findById(String id);
    OrganizationListModel findAll(int offset, int limit);
    OrganizationListModel findByCommonName(String commonName, int offset, int limit);
    OrganizationListModel findBySerialNumber(String serialNumber, int offset, int limit);

}
