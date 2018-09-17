package com.godaddy.evapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.godaddy.evapi.model.BlacklistListModel;
import com.godaddy.evapi.model.BlacklistModel;
import com.godaddy.evapi.model.OrganizationListModel;
import com.godaddy.evapi.model.OrganizationModel;

public class TestBlacklistService implements IBlacklistService {

    @Override
    public BlacklistModel findById(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlacklistListModel findAll(int offset, int limit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlacklistListModel findByCommonName(String commonName, int offset, int limit) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public static BlacklistListModel generateBlacklistList() {
        BlacklistListModel blList = new BlacklistListModel();
        blList.setCount(1);
        blList.setOffset(0);
        blList.setLimit(25);
        List<BlacklistModel> entries = new ArrayList<BlacklistModel>();
        entries.add(generateBlacklist());
        blList.setBlacklistEntries(entries);
        return blList;
    }
    
    public static BlacklistModel generateBlacklist() {
        BlacklistModel blModel = new BlacklistModel();

        blModel.setCommonName("example.com");
        blModel.setId(UUID.randomUUID());
        blModel.setOrganizationName("My Org");
        blModel.setSerialNumber("123456");
        
        return blModel;
    }
}
