package com.godaddy.evapi.service;

import java.util.ArrayList;
import java.util.Date;
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
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.http.AWSRequestSigningApacheInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.evapi.model.TopSiteListModel;
import com.godaddy.evapi.model.TopSiteModel;

@SuppressWarnings("deprecation")
@Service
public class TopSitesService extends BaseAWSService implements ITopSitesService {    
    @Autowired
    private RestHighLevelClient restClient;
    
    static final String INDEX = "topsites";
    static final String TYPE = "record";

    private ObjectMapper objectMapper = new ObjectMapper();  
    
    @Override
    // Create/Post
    // Write a new record to the index
    public boolean save(TopSiteModel tsModel) {
        boolean result = false;

        // We need to take out id, since ES stores it as _id. Would duplicate the data.
        Map data = objectMapper.convertValue(tsModel, Map.class);
        data.remove("id");
        try {
            IndexRequest request = new IndexRequest(INDEX, TYPE, tsModel.getId().toString()).source(data);
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
    
    @Override
    // Delete
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
    public boolean deleteByDateAndUrl(Date date, String url) {
        boolean result = false;
        try {
            QueryBuilder rangeQuery = QueryBuilders.rangeQuery("lastUpdated").lte(date);
            QueryBuilder matchQuery = QueryBuilders.matchQuery("url", url);
            QueryBuilder query = QueryBuilders.boolQuery().must(matchQuery).must(rangeQuery);

            DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(INDEX).types(TYPE);
            deleteByQueryRequest.setQuery( query );
            restClient.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return result;
    }
    
    @Override
    public boolean deleteByDate(Date date) {
        boolean result = false;
        try {
            QueryBuilder rangeQuery = QueryBuilders.rangeQuery("lastUpdated").lte(date);
            QueryBuilder query = QueryBuilders.boolQuery().must(rangeQuery);

            DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(INDEX).types(TYPE);
            deleteByQueryRequest.setQuery( query );
            restClient.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return result;
    }
    
    @Override
    // Read/Get
    public TopSiteModel findById(String id) {
        TopSiteModel topSite = null;
        
        try {
            GetRequest request = new GetRequest(INDEX, TYPE, id);
            GetResponse response = restClient.get(request);
            if(response.isExists()) {
                String data = response.getSourceAsString();
                ObjectMapper objectMapper = new ObjectMapper();
                    topSite = objectMapper.readValue(data, TopSiteModel.class);
                    // id is stored as _id, so we need to grab it
                    topSite.setId(UUID.fromString(response.getId()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return topSite;
    }
    
    @Override
    public TopSiteListModel findAll(int offset, int limit) {
        try {
            SearchRequest request = generateSearchRequest(QueryBuilders.matchAllQuery(), offset, limit, INDEX, TYPE);
            SearchResponse response = restClient.search(request);
            return findRecords(response, offset, limit);
        } catch (Exception ex) {
            return null;
        }
    }
    
    @Override
    public TopSiteListModel findByUrl(String url, int offset, int limit) {
        try {
            SearchRequest request = generateSearchRequest(QueryBuilders.matchQuery("url", url), offset, limit, INDEX, TYPE);
            SearchResponse response = restClient.search(request);
            return findRecords(response, offset, limit);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public TopSiteListModel findByDate(Date date,int offset, int limit) {
        TopSiteListModel records = null;
        try {
            QueryBuilder rangeQuery = QueryBuilders.rangeQuery("lastUpdated").lte(date);
            QueryBuilder query = QueryBuilders.boolQuery().must(rangeQuery);
            SearchRequest request = generateSearchRequest(query, offset, limit, INDEX, TYPE);
            SearchResponse response = restClient.search(request);
            return findRecords(response, offset, limit);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return records;
    }

 
    // PRIVATE FUNCTION CALLS / HELPERS
    
    private TopSiteListModel findRecords(SearchResponse response, int offset, int limit) {
        TopSiteListModel topSiteList = new TopSiteListModel();
        List<TopSiteModel> topSites = new ArrayList<TopSiteModel>();
        if(response.getHits().totalHits > 0) {
            SearchHit[] hits = response.getHits().getHits();
            try {                
                for(int ii = 0; ii < hits.length; ii++) {
                    TopSiteModel tsModel = objectMapper.readValue(hits[ii].getSourceAsString(), TopSiteModel.class);
                    // id is stored as _id, we need to do a translation
                    tsModel.setId(UUID.fromString(hits[ii].getId()));
                    topSites.add(tsModel);
                }
                
                topSiteList.setTopSites(topSites);
                topSiteList.setCount(topSites.size());
                topSiteList.setOffset(offset);
                topSiteList.setLimit(limit);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        return topSiteList;
    }

}
