package com.godaddy.evapi.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.godaddy.evapi.model.TopSiteListModel;
import com.godaddy.evapi.model.TopSiteModel;

public class TestTopSitesService implements ITopSitesService {

    @Override
    public boolean save(TopSiteModel site) {
        // Do nothing
        return true;
    }

    @Override
    public boolean delete(String id) {
        // Do nothing
        return true;
    }

    @Override
    public boolean deleteByDate(Date date) {
        // Do nothing
        return true;
    }

    @Override
    public boolean deleteByDateAndUrl(Date date, String url) {
        // Do nothing
        return true;
    }

    @Override
    public TopSiteModel findById(String id) {
        TopSiteModel model = new TopSiteModel();
        model.setId(UUID.fromString(id));
        model.setLastUpdated(new Date());
        model.setUrl("example.com");
        return model;
    }

    @Override
    public TopSiteListModel findAll(int offset, int limit) {
        TopSiteListModel list = new TopSiteListModel();
        List<TopSiteModel> tsmList = new ArrayList<TopSiteModel>();
        tsmList.add(findById(UUID.randomUUID().toString()));
        list.setTopSites(tsmList);
        list.setLimit(limit);
        list.setOffset(offset);
        list.setCount(tsmList.size());
        return list;
    }

    @Override
    public TopSiteListModel findByUrl(String url, int offset, int limit) {
        return findAll(offset, limit);
    }

    @Override
    public TopSiteListModel findByDate(Date date, int offset, int limit) {
        return findAll(offset, limit);
    }
    
    public static TopSiteModel GenerateTopSiteModel() {
        TopSiteModel model = new TopSiteModel();
        model.setId(UUID.randomUUID());
        model.setLastUpdated(new Date());
        model.setUrl("example.com");
        
        return model;
    }

    public static TopSiteModel GenerateTopSiteModel(String url) {
        TopSiteModel model = new TopSiteModel();
        model.setId(UUID.randomUUID());
        model.setLastUpdated(new Date());
        model.setUrl(url);
        
        return model;
    }

    public static TopSiteListModel GenerateTopSiteListModel() {
        TopSiteListModel list = new TopSiteListModel();
        List<TopSiteModel> tsmList = new ArrayList<TopSiteModel>();
        tsmList.add(GenerateTopSiteModel());
        tsmList.add(GenerateTopSiteModel("yahoo.com"));
        tsmList.add(GenerateTopSiteModel("google.com"));
        tsmList.add(GenerateTopSiteModel("microsoft.com"));
        tsmList.add(GenerateTopSiteModel("apple.com"));
        tsmList.add(GenerateTopSiteModel("youtube.com"));
        tsmList.add(GenerateTopSiteModel("facebook.com"));
        list.setTopSites(tsmList);
        list.setLimit(5);
        list.setOffset(0);
        list.setCount(tsmList.size());
        return list;
    }

}
