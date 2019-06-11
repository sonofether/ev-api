package com.godaddy.evapi.service;

import java.util.ArrayList;
import java.util.Date;
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
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.evapi.model.FlaglistListModel;
import com.godaddy.evapi.model.FlaglistModel;

@SuppressWarnings("deprecation")
@Service
public class FlaglistService  extends BaseAWSService implements IFlaglistService {
    @Autowired
    RestHighLevelClient restClient;
    
    static final String INDEX = "flaglist";
    static final String TYPE = "record";
    
    private ObjectMapper objectMapper = new ObjectMapper();  
        
    // Create/Post
    // Write a new record to the index
    @Override
    public boolean save(FlaglistModel flModel) {
        boolean result = false;

        // We need to take out id, since ES stores it as _id. Would duplicate the data.
        Map data = objectMapper.convertValue(flModel, Map.class);
        data.remove("id");
        try {
            IndexRequest request = new IndexRequest(INDEX, TYPE, flModel.getId().toString()).source(data);
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
    
    @Override
    public boolean deleteByDateAndSource(Date date, String source) {
        boolean result = false;
        try {
            QueryBuilder rangeQuery = QueryBuilders.rangeQuery("lastUpdated").lte(date);
            QueryBuilder matchQuery = QueryBuilders.matchQuery("source", source);
            QueryBuilder query = QueryBuilders.boolQuery().must(matchQuery).must(rangeQuery);

            DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(INDEX).types(TYPE);
            deleteByQueryRequest.setQuery( query );
            restClient.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return result;
    }


    
    // Read/Get
    @Override
    public FlaglistModel findById(String id) {
        FlaglistModel flaglist = null;
        
        try {
            GetRequest request = new GetRequest(INDEX, TYPE, id);
            GetResponse response = restClient.get(request);
            if(response.isExists()) {
                String data = response.getSourceAsString();
                ObjectMapper objectMapper = new ObjectMapper();
                flaglist = objectMapper.readValue(data, FlaglistModel.class);
                // id is stored as _id, so we need to grab it
                flaglist.setId(UUID.fromString(response.getId()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return flaglist;
    }
    
    @Override
    public FlaglistListModel findAll(int offset, int limit) {
        try {
            SearchRequest request = generateSearchRequest(QueryBuilders.matchAllQuery(), offset, limit, INDEX, TYPE);
            SearchResponse response = restClient.search(request);
            return findRecords(response, offset, limit);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public FlaglistListModel findByOrganizationName(String organizationName, int offset, int limit) {
        try {
            SearchRequest request = generateSearchRequest(QueryBuilders.matchQuery("organizationName", organizationName), offset, limit, INDEX, TYPE);
            SearchResponse response = restClient.search(request);
            return findRecords(response, offset, limit);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public FlaglistListModel findByCommonName(String commonName, int offset, int limit) {
        try {
            SearchRequest request = generateSearchRequest(QueryBuilders.matchQuery("commonName", commonName), offset, limit, INDEX, TYPE);
            SearchResponse response = restClient.search(request);
            return findRecords(response, offset, limit);
        } catch (Exception ex) {
            return null;
        }
    }
    
    @Override
    public FlaglistListModel findByCA(String ca, int offset, int limit) {
        try {
            SearchRequest request = generateSearchRequest(QueryBuilders.matchQuery("insertedBy", ca), offset, limit, INDEX, TYPE);
            SearchResponse response = restClient.search(request);
            return findRecords(response, offset, limit);
        } catch (Exception ex) {
            return null;
        }
    }
    
    @Override
    public FlaglistListModel findBySource(String source, int offset, int limit) {
        try {
            SearchRequest request = generateSearchRequest(QueryBuilders.matchQuery("source", source), offset, limit, INDEX, TYPE);
            SearchResponse response = restClient.search(request);
            return findRecords(response, offset, limit);
        } catch (Exception ex) {
            return null;
        }
    }
    
    @Override
    public FlaglistListModel findByDateAndSource(Date date, String source, int offset, int limit) {
        try {
            QueryBuilder rangeQuery = QueryBuilders.rangeQuery("lastUpdated").lte(date);
            QueryBuilder matchQuery = QueryBuilders.matchQuery("source", source);
            QueryBuilder query = QueryBuilders.boolQuery().must(matchQuery).must(rangeQuery);
            SearchRequest request = generateSearchRequest(query, offset, limit, INDEX, TYPE);
            SearchResponse response = restClient.search(request);
            return findRecords(response, offset, limit);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public FlaglistListModel findByVariableArguments(String filter, int offset, int limit) {
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

 
    // PRIVATE FUNCTION CALLS / HELPERS
    
    private FlaglistListModel findRecords(SearchResponse response, int offset, int limit) {
        FlaglistListModel flaglistList = new FlaglistListModel();
        List<FlaglistModel> flaglist = new ArrayList<FlaglistModel>();
        if(response.getHits().totalHits > 0) {
            SearchHit[] hits = response.getHits().getHits();
            try {                
                for(int ii = 0; ii < hits.length; ii++) {
                    FlaglistModel flModel = objectMapper.readValue(hits[ii].getSourceAsString(), FlaglistModel.class);
                    // id is stored as _id, we need to do a translation
                    flModel.setId(UUID.fromString(hits[ii].getId()));
                    flaglist.add(flModel);
                }
                
                flaglistList.setFlaglistEntries(flaglist);
                flaglistList.setCount(flaglist.size());
                flaglistList.setOffset(offset);
                flaglistList.setLimit(limit);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        return flaglistList;
    }
 
}
