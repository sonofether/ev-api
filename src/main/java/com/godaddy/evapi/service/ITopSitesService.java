package com.godaddy.evapi.service;

import java.util.Date;

import com.godaddy.evapi.model.TopSiteListModel;
import com.godaddy.evapi.model.TopSiteModel;

public interface ITopSitesService {
    // Create/Update
    boolean save(TopSiteModel site);
    
    // Delete
    boolean delete(String id);
    boolean deleteByDate(Date date);
    boolean deleteByDateAndUrl(Date date, String url);
    
    // Read/Get
    TopSiteModel findById(String id);
    TopSiteListModel findAll(int offset, int limit);
    TopSiteListModel findByUrl(String url, int offset, int limit);
    TopSiteListModel findByDate(Date date, int offset, int limit);
}
