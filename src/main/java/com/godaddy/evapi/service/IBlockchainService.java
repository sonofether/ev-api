package com.godaddy.evapi.service;

import com.godaddy.evapi.model.OrganizationListModel;
import com.godaddy.evapi.model.OrganizationModel;

public interface IBlockchainService {
    // Create/Update
    boolean save(OrganizationModel org);
    // Delete
    boolean delete(String id);
    // Read/Get
    void findById(String id);
    void findAll(int offset, int limit);
    void findByCName(String commonName);
    void findValidationItems(String id);
}
