package com.godaddy.evapi.service;

import com.godaddy.evapi.model.BlacklistListModel;
import com.godaddy.evapi.model.BlacklistModel;

public interface IBlacklistService {
    // Read/Get
    BlacklistModel findById(String id);
    BlacklistListModel findAll(int offset, int limit);
    BlacklistListModel findByCommonName(String commonName, int offset, int limit);
}
