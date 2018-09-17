package com.godaddy.evapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteResponse;
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
import com.godaddy.evapi.model.OrganizationListModel;
import com.godaddy.evapi.model.OrganizationModel;
import com.godaddy.evapi.repository.OrganizationRepository;

@Service
public class OrganizationService implements IOrganizationService {

    @Autowired
    TransportClient transportClient;
    
    static final String INDEX = "organization";
    static final String TYPE = "record";
    
    ObjectMapper objectMapper = new ObjectMapper();    

    // TODO Remove this
    //private OrganizationRepository orgRepository;
    
    //public void setOrganizationRepository(OrganizationRepository organizationRepository) {
    //    this.orgRepository = organizationRepository;
    //}
    
    @Override
    public OrganizationModel save(OrganizationModel org) {
        // TODO Auto-generated method stub
        //return (OrganizationModel) orgRepository.save(org);
        //IndexRequest request = new IndexRequest("organization", "record", org.getId()).source(ObjectMapper.convertValue(org, Map.class));
        
        return null;
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

    // Return a single record by id
    @Override
    public OrganizationModel findById(String id) {
        OrganizationModel org = null;
        
        GetRequest request = new GetRequest(INDEX, TYPE, id);
        GetResponse response = transportClient.get(request).actionGet();
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

        return org;
    }
    
    @Override
    public OrganizationListModel findAll(int offset, int limit) {
        SearchResponse response = transportClient.prepareSearch(INDEX).setTypes(TYPE).setFrom(offset).setSize(limit).get();
        return findRecords(response, offset, limit);
    }
    
    @Override
    public OrganizationListModel findByCommonName(String commonName, int offset, int limit) {
        SearchResponse response = transportClient.prepareSearch(INDEX)
                    .setTypes(TYPE)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(QueryBuilders.matchQuery("commonName", commonName))
                    .setFrom(offset).setSize(limit).setExplain(true)
                    .get();
        return findRecords(response, offset, limit);
    }
    
    @Override
    public OrganizationListModel findByOrganizationName(String orgName, int offset, int limit) {
        SearchResponse response = transportClient.prepareSearch(INDEX)
                    .setTypes(TYPE)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(QueryBuilders.matchQuery("organizationName", orgName))
                    .setFrom(offset).setSize(limit).setExplain(true)
                    .get();
        return findRecords(response, offset, limit);
    }
    
    @Override
    public OrganizationListModel findBySerialNumber(String serialNumber, int offset, int limit) {
        SearchResponse response = transportClient.prepareSearch(INDEX)
                    .setTypes(TYPE)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(QueryBuilders.matchQuery("serialNumber", serialNumber))
                    .setFrom(offset).setSize(limit).setExplain(true)
                    .get();
        return findRecords(response, offset, limit);        
    }
    
    @Override
    public OrganizationListModel findByNameSerialNumberCountry(String name, String serialNumber, String country, int offset, int limit) {
        SearchResponse response = transportClient.prepareSearch(INDEX)
                    .setTypes(TYPE)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("commonName", name))
                                .must(QueryBuilders.matchQuery("serialNumber", serialNumber))
                                .must(QueryBuilders.matchQuery("countryName", country)))
                    .setFrom(offset).setSize(limit).setExplain(true)
                    .get();
        return findRecords(response, offset, limit);
    }
        
    @Override
    public OrganizationListModel findByNameSerialNumberCountryState(String name, String serialNumber, String country, String state, int offset, int limit) {
        SearchResponse response = transportClient.prepareSearch(INDEX)
                    .setTypes(TYPE)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("commonName", name))
                                .must(QueryBuilders.matchQuery("serialNumber", serialNumber))
                                .must(QueryBuilders.matchQuery("countryName", country))
                                .must(QueryBuilders.matchQuery("stateOrProvinceName", state)))
                    .setFrom(offset).setSize(limit).setExplain(true)
                    .get();
        return findRecords(response, offset, limit);
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
