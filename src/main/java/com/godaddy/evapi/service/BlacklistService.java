package com.godaddy.evapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.http.AWSRequestSigningApacheInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.evapi.model.BlacklistListModel;
import com.godaddy.evapi.model.BlacklistModel;

@Service
public class BlacklistService extends BaseAWSService implements IBlacklistService {
    @Autowired
    RestHighLevelClient restClient;
    
    static final String INDEX = "blacklist";
    static final String TYPE = "record";
    
    private ObjectMapper objectMapper = new ObjectMapper();  
        
    // Create/Post
    // Write a new record to the index
    @Override
    public boolean save(BlacklistModel blModel) {
        boolean result = false;

        // We need to take out id, since ES stores it as _id. Would duplicate the data.
        Map data = objectMapper.convertValue(blModel, Map.class);
        data.remove("id");
        try {
            IndexRequest request = new IndexRequest(INDEX, TYPE, blModel.getId().toString()).source(data);
            IndexResponse response = restClient.index(request);
            if(response.getResult() == DocWriteResponse.Result.CREATED || 
               response.getResult() == DocWriteResponse.Result.UPDATED) {
                result = true;
            }
        } catch(Exception ex) {
            
        }
        
        return result;
    }
    
    // Delete
    @Override
    public boolean delete(String id) {
        boolean result = false;
        try {
            DeleteRequest deleteRequest = new DeleteRequest(INDEX, TYPE, id);
            DeleteResponse response = restClient.delete(deleteRequest);
            if(response != null && response.getResult() == DocWriteResponse.Result.DELETED) {
                // Hey, it worked!
                result = true;
            }
        } catch (Exception ex) {
            
        }
        
        return result;
    }
    
    // Read/Get
    @Override
    public BlacklistModel findById(String id) {
        BlacklistModel blacklist = null;
        
        try {
            GetRequest request = new GetRequest(INDEX, TYPE, id);
            GetResponse response = restClient.get(request);
            if(response.isExists()) {
                String data = response.getSourceAsString();
                ObjectMapper objectMapper = new ObjectMapper();
                    blacklist = objectMapper.readValue(data, BlacklistModel.class);
                    // id is stored as _id, so we need to grab it
                    blacklist.setId(UUID.fromString(response.getId()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return blacklist;
    }
    
    @Override
    public BlacklistListModel findAll(int offset, int limit) {
        try {
            SearchRequest request = generateSearchRequest(QueryBuilders.matchAllQuery(), offset, limit, INDEX, TYPE);
            SearchResponse response = restClient.search(request);
            return findRecords(response, offset, limit);
        } catch (Exception ex) {
            return null;
        }
    }
    
    @Override
    public BlacklistListModel findByCommonName(String commonName, int offset, int limit) {
        try {
            SearchRequest request = generateSearchRequest(QueryBuilders.matchQuery("commonName", commonName), offset, limit, INDEX, TYPE);
            SearchResponse response = restClient.search(request);
            return findRecords(response, offset, limit);
        } catch (Exception ex) {
            return null;
        }
    }
    
    @Override
    public BlacklistListModel findByCA(String ca, int offset, int limit) {
        try {
            SearchRequest request = generateSearchRequest(QueryBuilders.matchQuery("insertedBy", ca), offset, limit, INDEX, TYPE);
            SearchResponse response = restClient.search(request);
            return findRecords(response, offset, limit);
        } catch (Exception ex) {
            return null;
        }
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