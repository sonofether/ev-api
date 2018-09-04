package com.godaddy.evapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
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
    
    ObjectMapper objectMapper = new ObjectMapper();    

    private OrganizationRepository orgRepository;
    
    public void setOrganizationRepository(OrganizationRepository organizationRepository) {
        this.orgRepository = organizationRepository;
    }
    
    @Override
    public OrganizationModel save(OrganizationModel org) {
        // TODO Auto-generated method stub
        //return (OrganizationModel) orgRepository.save(org);
        //IndexRequest request = new IndexRequest("organization", "record", org.getId()).source(ObjectMapper.convertValue(org, Map.class));
        
        return null;
    }

    @Override
    public void delete(OrganizationModel org) {
        // TODO Auto-generated method stub
        //orgRepository.delete(org);
    }

    @Override
    public OrganizationModel findById(String id) {
        OrganizationModel org = null;
        
        GetRequest request = new GetRequest("organization", "record", id);
        GetResponse response = transportClient.get(request).actionGet();
        if(response.isExists()) {
            String data = response.getSourceAsString();
            ObjectMapper objectMapper = new ObjectMapper();
            // Dump out some text.
            System.out.println(data);
            try {
                org = objectMapper.readValue(data, OrganizationModel.class);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                System.out.println("ASINK: EXCEPTION MAPPING DATA!!!!!");
            }
        }

        return org;
    }
    
    @Override
    public OrganizationListModel findAll(int offset, int limit) {
        SearchResponse response = transportClient.prepareSearch("organization").setTypes("record").setFrom(offset).setSize(limit).get();
        return findRecords(response, offset, limit);
    }
    
    @Override
    public OrganizationListModel findByCommonName(String commonName, int offset, int limit) {
        SearchResponse response = transportClient.prepareSearch("organization")
                    .setTypes("record")
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(QueryBuilders.matchQuery("commonName", commonName))
//                    .setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))     // Filter
                    .setFrom(offset).setSize(limit).setExplain(true)
                    .get();
        return findRecords(response, offset, limit);
    }
    
    @Override
    public OrganizationListModel findBySerialNumber(String serialNumber, int offset, int limit) {
        SearchResponse response = transportClient.prepareSearch("organization")
                    .setTypes("record")
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(QueryBuilders.matchQuery("serialNumber", serialNumber))
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
                    orgs.add(objectMapper.readValue(hits[ii].getSourceAsString(), OrganizationModel.class));
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
/*
    @Override
    public List<OrganizationModel> findByOrganizationName(String organizationName) {
        // TODO Auto-generated method stub
        return orgRepository.findByOrganizationName(organizationName);
    }
*/    
}
