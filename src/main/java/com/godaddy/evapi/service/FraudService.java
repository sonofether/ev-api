package com.godaddy.evapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.evapi.model.FlaglistListModel;
import com.godaddy.evapi.model.FraudListModel;
import com.godaddy.evapi.model.FraudModel;

@SuppressWarnings("deprecation")
@Service
public class FraudService extends BaseAWSService implements IFraudService{
    @Autowired
    RestHighLevelClient restClient;
    
    static final String INDEX = "fraud";
    static final String TYPE = "record";
    
    private ObjectMapper objectMapper = new ObjectMapper();  
        
    // Create/Post
    // Write a new record to the index
    @Override
    public boolean save(FraudModel fraudModel) {
        boolean result = false;

        // We need to take out id, since ES stores it as _id. Would duplicate the data.
        Map data = objectMapper.convertValue(fraudModel, Map.class);
        data.remove("id");
        try {
            IndexRequest request = new IndexRequest(INDEX, TYPE, fraudModel.getId().toString()).source(data);
            IndexResponse response = restClient.index(request);
            if(response.getResult() == DocWriteResponse.Result.CREATED || 
               response.getResult() == DocWriteResponse.Result.UPDATED) {
                result = true;
            }
        } catch(Exception ex) {
            ex.printStackTrace();
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
            ex.printStackTrace();
        }
        
        return result;
    }
    
 // Read/Get
    @Override
    public FraudModel findById(String id) {
        FraudModel fraud = null;
        
        try {
            GetRequest request = new GetRequest(INDEX, TYPE, id);
            GetResponse response = restClient.get(request);
            if(response.isExists()) {
                String data = response.getSourceAsString();
                ObjectMapper objectMapper = new ObjectMapper();
                fraud = objectMapper.readValue(data, FraudModel.class);
                    // id is stored as _id, so we need to grab it
                fraud.setId(UUID.fromString(response.getId()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return fraud;
    }
    
    @Override
    public FraudListModel findAll(int offset, int limit) {
        try {
            SearchRequest request = generateSearchRequest(QueryBuilders.matchAllQuery(), offset, limit, INDEX, TYPE);
            SearchResponse response = restClient.search(request);
            return findRecords(response, offset, limit);
        } catch (Exception ex) {
            return null;
        }
    }
    
    @Override
    public FraudListModel findByKeyword(String keyword, int offset, int limit) {
        try {
            SearchRequest request = generateSearchRequest(QueryBuilders.matchQuery("keyword", keyword), offset, limit, INDEX, TYPE);
            SearchResponse response = restClient.search(request);
            return findRecords(response, offset, limit);
        } catch (Exception ex) {
            return null;
        }
    }
    
    @Override
    public FraudListModel findByVariableArguments(String filter, int offset, int limit) {
        QueryBuilder query = buildQueryFromFilters(filter.trim());
        try {
            SearchRequest request = generateSearchRequest(query, offset, limit, INDEX, TYPE);
            SearchResponse response = restClient.search(request);
            return findRecords(response, offset, limit);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return null;
    }
    
    // PRIVATE FUNCTIONS
    
    private FraudListModel findRecords(SearchResponse response, int offset, int limit) {
        FraudListModel fraudListModel = new FraudListModel();
        List<FraudModel> fraudList = new ArrayList<FraudModel>();
        if(response.getHits().totalHits > 0) {
            SearchHit[] hits = response.getHits().getHits();
            try {                
                for(int ii = 0; ii < hits.length; ii++) {
                    FraudModel fraudModel = objectMapper.readValue(hits[ii].getSourceAsString(), FraudModel.class);
                    // id is stored as _id, we need to do a translation
                    fraudModel.setId(UUID.fromString(hits[ii].getId()));
                    fraudList.add(fraudModel);
                }
                
                fraudListModel.setFraudList(fraudList);
                fraudListModel.setCount(fraudList.size());
                fraudListModel.setOffset(offset);
                fraudListModel.setLimit(limit);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        return fraudListModel;
    }
    
}
