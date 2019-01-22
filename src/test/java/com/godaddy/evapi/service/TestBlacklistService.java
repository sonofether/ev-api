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
        return generateBlacklist();
    }

    @Override
    public BlacklistListModel findAll(int offset, int limit) {
        return generateBlacklistList();
    }

    @Override
    public BlacklistListModel findByCommonName(String commonName, int offset, int limit) {
        return generateBlacklistList();
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

    // TODO Add these function tests
    @Override
    public boolean save(BlacklistModel blModel) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean delete(String id) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public BlacklistListModel findByCA(String ca, int offset, int limit) {
        return generateBlacklistList();
    }
}
