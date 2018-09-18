package com.godaddy.evapi.service;

import java.util.List;

import com.godaddy.evapi.model.OrganizationListModel;
import com.godaddy.evapi.model.OrganizationModel;

public interface IOrganizationService {

    // Create/Update
    boolean save(OrganizationModel org);
    // Delete
    boolean delete(String id);
    // Read/Get
    OrganizationModel findById(String id);
    OrganizationListModel findAll(int offset, int limit);
    OrganizationListModel findByCommonName(String commonName, int offset, int limit);
    OrganizationListModel findByOrganizationName(String orgName, int offset, int limit);
    OrganizationListModel findBySerialNumber(String serialNumber, int offset, int limit);
    OrganizationListModel findByNameSerialNumberCountry(String name, String serialNumber, String country, int offset, int limit);
    OrganizationListModel findByNameSerialNumberCountryState(String name, String serialNumber, String country, String state, int offset, int limit);
}
