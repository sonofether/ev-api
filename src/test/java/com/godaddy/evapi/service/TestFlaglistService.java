package com.godaddy.evapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.godaddy.evapi.model.FlaglistListModel;
import com.godaddy.evapi.model.FlaglistModel;

public class TestFlaglistService implements IFlaglistService {

    @Override
    public FlaglistModel findById(String id) {
        return generateFlaglist();
    }

    @Override
    public FlaglistListModel findAll(int offset, int limit) {
        return generateFlaglistList();
    }

    @Override
    public FlaglistListModel findByCommonName(String commonName, int offset, int limit) {
        return generateFlaglistList();
    }
    
    @Override
    public FlaglistListModel findByOrganizationName(String organizationName, int offset, int limit) {
        return generateFlaglistList();
    }

    public static FlaglistListModel generateFlaglistList() {
        FlaglistListModel flList = new FlaglistListModel();
        flList.setCount(1);
        flList.setOffset(0);
        flList.setLimit(25);
        List<FlaglistModel> entries = new ArrayList<FlaglistModel>();
        entries.add(generateFlaglist());
        flList.setFlaglistEntries(entries);
        return flList;
    }
    
    public static FlaglistModel generateFlaglist() {
        FlaglistModel flModel = new FlaglistModel();

        flModel.setCommonName("example.com");
        flModel.setId(UUID.randomUUID());
        flModel.setOrganizationName("My Org");
        flModel.setSerialNumber("123456");
        
        return flModel;
    }

    // TODO Add these function tests
    @Override
    public boolean save(FlaglistModel flModel) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean delete(String id) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public FlaglistListModel findByCA(String ca, int offset, int limit) {
        return generateFlaglistList();
    }

    @Override
    public FlaglistListModel findBySource(String source, int offset, int limit) {
        return generateFlaglistList();
    }
}
