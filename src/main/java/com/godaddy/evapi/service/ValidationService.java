package com.godaddy.evapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.evapi.model.OrganizationListModel;
import com.godaddy.evapi.model.OrganizationModel;
import com.godaddy.evapi.model.ValidationInputModel;
import com.godaddy.evapi.model.ValidationItemModel;
import com.godaddy.evapi.model.ValidationListModel;

@Service
public class ValidationService implements IValidationService {
    
    @Autowired
    TransportClient transportClient;
    
    static final String INDEX = "validation";
    static final String TYPE = "record";
    
    ObjectMapper objectMapper = new ObjectMapper();    

    @Override
    public boolean save(ValidationItemModel vi) {
        boolean result = false;
        // We need to take out id, since ES stores it as _id. Would duplicate the data.
        Map data = objectMapper.convertValue(vi, Map.class);
        data.remove("id");
        IndexRequest request = new IndexRequest(INDEX, TYPE, vi.getId().toString()).source(data);
        IndexResponse response = transportClient.index(request).actionGet();
        if(response.getResult() == DocWriteResponse.Result.CREATED || 
           response.getResult() == DocWriteResponse.Result.UPDATED) {
            result = true;
        }
        return result;
    }

    @Override
    public boolean delete(String id) {
        DeleteResponse response = transportClient.prepareDelete(INDEX, TYPE, id).get();
        if(response != null && response.getResult() == DocWriteResponse.Result.DELETED) {
            // Hey, it worked!
            return true;
        }
        return false;
    }

    @Override
    public ValidationItemModel findById(String id) {
        ValidationItemModel vi = null;
        
        GetRequest request = new GetRequest(INDEX, TYPE, id);
        GetResponse response = transportClient.get(request).actionGet();
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

        return vi;
    }

    @Override
    public ValidationListModel findAll(int offset, int limit) {
        SearchResponse response = transportClient.prepareSearch(INDEX).setTypes(TYPE).setFrom(offset).setSize(limit).get();
        return findRecords(response, offset, limit);
    }
    
    @Override
    public ValidationListModel findByCertificateId(String certificateId, int offset, int limit) {
        SearchResponse response = transportClient.prepareSearch(INDEX)
                    .setTypes(TYPE)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(QueryBuilders.matchQuery("certificateId", certificateId))
                    .setFrom(offset).setSize(limit).setExplain(true)
                    .get();
        return findRecords(response, offset, limit);

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