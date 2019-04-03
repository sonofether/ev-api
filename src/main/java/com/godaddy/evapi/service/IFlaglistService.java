package com.godaddy.evapi.service;

import java.util.Date;

import com.godaddy.evapi.model.FlaglistListModel;
import com.godaddy.evapi.model.FlaglistModel;

public interface IFlaglistService {
    // Create/Update
    boolean save(FlaglistModel blModel);
    
    // Delete
    boolean delete(String id);
    boolean deleteByDateAndSource(Date date, String source);
    
    // Read/Get
    FlaglistModel findById(String id);
    FlaglistListModel findAll(int offset, int limit);
    FlaglistListModel findByOrganizationName(String organizationName, int offset, int limit);
    FlaglistListModel findByCommonName(String commonName, int offset, int limit);
    FlaglistListModel findByCA(String ca, int offset, int limit);
    FlaglistListModel findBySource(String source, int offset, int limit);
    FlaglistListModel findByDateAndSource(Date date, String source, int offset, int limit);
    FlaglistListModel findByVariableArguments(String filter, int offset, int limit);

}
