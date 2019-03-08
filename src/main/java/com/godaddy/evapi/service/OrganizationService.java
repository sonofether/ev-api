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
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.evapi.model.OrganizationListModel;
import com.godaddy.evapi.model.OrganizationModel;

@Service
public class OrganizationService extends BaseAWSService implements IOrganizationService {

    @Autowired
    TransportClient transportClient;
    
    @Autowired
    RestHighLevelClient restClient;
        
    static final String INDEX = "organization";
    static final String TYPE = "record";
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    // Write a new record to the index
    @Override
    public boolean save(OrganizationModel org) {
        boolean result = false;
        // We need to take out id, since ES stores it as _id. Would duplicate the data.
        Map data = objectMapper.convertValue(org, Map.class);
        data.remove("id");
        try {
            IndexRequest request = new IndexRequest(INDEX, TYPE, org.getId().toString()).source(data);
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

    // Return a single record by id
    @Override
    public OrganizationModel findById(String id) {
        OrganizationModel org = null;
        
        try {
            GetRequest request = new GetRequest(INDEX, TYPE, id);
            GetResponse response = restClient.get(request);
            if(response.isExists()) {
                String data = response.getSourceAsString();
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    org = objectMapper.readValue(data, OrganizationModel.class);
                    // id is stored as _id, so we need to grab it
                    org.setId(UUID.fromString(response.getId()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return org;
    }
    
    @Override
    public OrganizationListModel findAll(int offset, int limit) {
        try {
            SearchRequest request = generateSearchRequest(QueryBuilders.matchAllQuery(), offset, limit, INDEX, TYPE);
            SearchResponse response = restClient.search(request);
            return findRecords(response, offset, limit);
        } catch (Exception ex) {
            
        }
        
        return null;
    }
    
    @Override
    public OrganizationListModel findByCommonName(String commonName, int offset, int limit) {
        try {
            SearchRequest request = generateSearchRequest(QueryBuilders.matchQuery("commonName", commonName), offset, limit, INDEX, TYPE);
            SearchResponse response = restClient.search(request);
            return findRecords(response, offset, limit);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public OrganizationListModel findByOrganizationName(String orgName, int offset, int limit) {
        try {
            SearchRequest request = generateSearchRequest(QueryBuilders.matchQuery("organizationName", orgName), offset, limit, INDEX, TYPE);
            SearchResponse response = restClient.search(request);
            return findRecords(response, offset, limit);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public OrganizationListModel findBySerialNumber(String serialNumber, int offset, int limit) {
        try {
            SearchRequest request = generateSearchRequest(QueryBuilders.matchQuery("serialNumber", serialNumber), offset, limit, INDEX, TYPE);
            SearchResponse response = restClient.search(request);
            return findRecords(response, offset, limit);        
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public OrganizationListModel findByNameSerialNumberCountry(String name, String serialNumber, String country, int offset, int limit) {
        try {
            SearchRequest request = generateSearchRequest(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("commonName", name))
                        .must(QueryBuilders.matchQuery("serialNumber", serialNumber))
                        .must(QueryBuilders.matchQuery("countryName", country)),
                        offset, limit, INDEX, TYPE);
            SearchResponse response = restClient.search(request);
            return findRecords(response, offset, limit);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return null;
    }
        
    @Override
    public OrganizationListModel findByNameSerialNumberCountryState(String name, String serialNumber, String country, String state, int offset, int limit) {
        try {
            SearchRequest request = generateSearchRequest(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("commonName", name))
                        .must(QueryBuilders.matchQuery("serialNumber", serialNumber))
                        .must(QueryBuilders.matchQuery("countryName", country))
                        .must(QueryBuilders.matchQuery("stateOrProvinceName", state)),
                        offset, limit, INDEX, TYPE);
            SearchResponse response = restClient.search(request);
            return findRecords(response, offset, limit);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return null;
    }
    
    // PRIVATE FUNCTION CALLS / HELPERS
    
    private OrganizationListModel findRecords(SearchResponse response, int offset, int limit) {
        OrganizationListModel orgList = new OrganizationListModel();
        List<OrganizationModel> orgs = new ArrayList<OrganizationModel>();
        if(response.getHits().totalHits > 0) {
            SearchHit[] hits = response.getHits().getHits();
            try {                
                for(int ii = 0; ii < hits.length; ii++) {
                    OrganizationModel orgModel = objectMapper.readValue(hits[ii].getSourceAsString(), OrganizationModel.class);
                    // id is stored as _id, we need to do a translation
                    orgModel.setId(UUID.fromString(hits[ii].getId()));
                    orgs.add(orgModel);
                }
                
                orgList.setOrganizations(orgs);
                orgList.setCount(orgs.size());
                orgList.setOffset(offset);
                orgList.setLimit(limit);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        return orgList;
    }

}
