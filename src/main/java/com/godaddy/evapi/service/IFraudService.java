package com.godaddy.evapi.service;

import com.godaddy.evapi.model.FlaglistListModel;
import com.godaddy.evapi.model.FraudListModel;
import com.godaddy.evapi.model.FraudModel;

public interface IFraudService {
    // Create/Update
    boolean save(FraudModel fraudModel);
    
    // Delete
    boolean delete(String id);
    
    // Read/Get
    FraudModel findById(String id);
    FraudListModel findAll(int offset, int limit);
    FraudListModel findByKeyword(String keyword, int offset, int limit);
    FraudListModel findByVariableArguments(String filter, int offset, int limit);
}
