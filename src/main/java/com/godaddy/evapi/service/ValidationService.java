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
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.http.AWSRequestSigningApacheInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.evapi.model.OrganizationListModel;
import com.godaddy.evapi.model.OrganizationModel;
import com.godaddy.evapi.model.ValidationInputModel;
import com.godaddy.evapi.model.ValidationItemModel;
import com.godaddy.evapi.model.ValidationListModel;

@Service
public class ValidationService extends BaseAWSService implements IValidationService {   
    @Autowired
    RestHighLevelClient restClient;
    
    static final String INDEX = "validation";
    static final String TYPE = "record";
    
    private ObjectMapper objectMapper = new ObjectMapper();    
    
    @Override
    public boolean save(ValidationItemModel vi) {
        boolean result = false;
        // We need to take out id, since ES stores it as _id. Would duplicate the data.
        Map data = objectMapper.convertValue(vi, Map.class);
        data.remove("id");
        try {
            IndexRequest request = new IndexRequest(INDEX, TYPE, vi.getId().toString()).source(data);
            IndexResponse response = restClient.index(request);
            if(response.getResult() == DocWriteResponse.Result.CREATED || 
               response.getResult() == DocWriteResponse.Result.UPDATED) {
                result = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean delete(String id) {
        try {
            DeleteRequest deleteRequest = new DeleteRequest(INDEX, TYPE, id);
            DeleteResponse response = restClient.delete(deleteRequest);
            if(response != null && response.getResult() == DocWriteResponse.Result.DELETED) {
                // Hey, it worked!
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public ValidationItemModel findById(String id) {
        ValidationItemModel vi = null;
        
        try {
            GetRequest request = new GetRequest(INDEX, TYPE, id);
            GetResponse response = restClient.get(request);
            if(response.isExists()) {
                String data = response.getSourceAsString();
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    vi = objectMapper.readValue(data, ValidationItemModel.class);
                    // id is stored as _id, so we need to grab it
                    vi.setId(UUID.fromString(response.getId()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return vi;
    }

    @Override
    public ValidationListModel findAll(int offset, int limit) {
        try {
            SearchRequest request = generateSearchRequest(QueryBuilders.matchAllQuery(), offset, limit, INDEX, TYPE);
            SearchResponse response = restClient.search(request);
            return findRecords(response, offset, limit);
        } catch(Exception ex) {
            
        }
        
        return null;
    }
    
    @Override
    public ValidationListModel findByCertificateId(String certificateId, int offset, int limit) {
        try {
            SearchRequest request = generateSearchRequest(QueryBuilders.matchQuery("certificateId", certificateId), offset, limit, INDEX, TYPE);
            SearchResponse response = restClient.search(request);
            return findRecords(response, offset, limit);
        } catch (Exception ex) {
            
        }
        
        return null;
    }

    // PRIVATE FUNCTION CALLS / HELPERS
    
    private ValidationListModel findRecords(SearchResponse response, int offset, int limit) {
        ValidationListModel viList = new ValidationListModel();
        List<ValidationItemModel> vis = new ArrayList<ValidationItemModel>();
        if(response.getHits().totalHits > 0) {
            SearchHit[] hits = response.getHits().getHits();
            try {                
                for(int ii = 0; ii < hits.length; ii++) {
                    ValidationItemModel viModel = objectMapper.readValue(hits[ii].getSourceAsString(), ValidationItemModel.class);
                    // id is stored as _id, we need to do a translation
                    viModel.setId(UUID.fromString(hits[ii].getId()));
                    vis.add(viModel);
                }
                
                viList.setValidationItems(vis);
                viList.setCount(vis.size());
                viList.setOffset(offset);
                viList.setLimit(limit);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        return viList;
    }
}