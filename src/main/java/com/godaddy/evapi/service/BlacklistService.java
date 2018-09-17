package com.godaddy.evapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.evapi.model.BlacklistListModel;
import com.godaddy.evapi.model.BlacklistModel;

@Service
public class BlacklistService implements IBlacklistService {
    @Autowired
    TransportClient transportClient;
    
    static final String INDEX = "blacklist";
    static final String TYPE = "record";
    
    ObjectMapper objectMapper = new ObjectMapper();    
    
    // Read/Get
    @Override
    public BlacklistModel findById(String id) {
        BlacklistModel blacklist = null;
        
        GetRequest request = new GetRequest(INDEX, TYPE, id);
        GetResponse response = transportClient.get(request).actionGet();
        if(response.isExists()) {
            String data = response.getSourceAsString();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                blacklist = objectMapper.readValue(data, BlacklistModel.class);
                // id is stored as _id, so we need to grab it
                blacklist.setId(UUID.fromString(response.getId()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return blacklist;
    }
    
    @Override
    public BlacklistListModel findAll(int offset, int limit) {
        SearchResponse response = transportClient.prepareSearch(INDEX).setTypes(TYPE).setFrom(offset).setSize(limit).get();
        return findRecords(response, offset, limit);
    }
    
    @Override
    public BlacklistListModel findByCommonName(String commonName, int offset, int limit) {
        SearchResponse response = transportClient.prepareSearch(INDEX)
                    .setTypes(TYPE)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(QueryBuilders.matchQuery("commonName", commonName))
                    .setFrom(offset).setSize(limit).setExplain(true)
                    .get();
        return findRecords(response, offset, limit);
    }
 
    // PRIVATE FUNCTION CALLS / HELPERS
    
    private BlacklistListModel findRecords(SearchResponse response, int offset, int limit) {
        BlacklistListModel blacklistList = new BlacklistListModel();
        List<BlacklistModel> blacklist = new ArrayList<BlacklistModel>();
        if(response.getHits().totalHits > 0) {
            SearchHit[] hits = response.getHits().getHits();
            try {                
                for(int ii = 0; ii < hits.length; ii++) {
                    BlacklistModel blModel = objectMapper.readValue(hits[ii].getSourceAsString(), BlacklistModel.class);
                    // id is stored as _id, we need to do a translation
                    blModel.setId(UUID.fromString(hits[ii].getId()));
                    blacklist.add(blModel);
                }
                
                blacklistList.setBlacklistEntries(blacklist);
                blacklistList.setCount(blacklist.size());
                blacklistList.setOffset(offset);
                blacklistList.setLimit(limit);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        return blacklistList;
    }
 
}
