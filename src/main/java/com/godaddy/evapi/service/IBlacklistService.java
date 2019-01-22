package com.godaddy.evapi.service;

import com.godaddy.evapi.model.BlacklistListModel;
import com.godaddy.evapi.model.BlacklistModel;

public interface IBlacklistService {
    // Create/Update
    boolean save(BlacklistModel blModel);
    
    // Delete
    boolean delete(String id);
    
    // Read/Get
    BlacklistModel findById(String id);
    BlacklistListModel findAll(int offset, int limit);
    BlacklistListModel findByCommonName(String commonName, int offset, int limit);
    BlacklistListModel findByCA(String ca, int offset, int limit);
}
