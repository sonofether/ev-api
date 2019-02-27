package com.godaddy.evapi.service;

import com.godaddy.evapi.model.FlaglistListModel;
import com.godaddy.evapi.model.FlaglistModel;

public interface IFlaglistService {
    // Create/Update
    boolean save(FlaglistModel blModel);
    
    // Delete
    boolean delete(String id);
    
    // Read/Get
    FlaglistModel findById(String id);
    FlaglistListModel findAll(int offset, int limit);
    FlaglistListModel findByCommonName(String commonName, int offset, int limit);
    FlaglistListModel findByCA(String ca, int offset, int limit);
}
