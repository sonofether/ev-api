package com.godaddy.evapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.evapi.model.CertificateListModel;
import com.godaddy.evapi.model.CertificateModel;
import com.godaddy.evapi.model.OrganizationListModel;
import com.godaddy.evapi.model.OrganizationModel;

@Service
public class CertificateService implements ICertificateService {

    @Autowired
    TransportClient transportClient;
    
    static final String INDEX = "certificate";
    static final String TYPE = "record";
    
    ObjectMapper objectMapper = new ObjectMapper();    

    @Override
    public CertificateModel save(CertificateModel certificate) {
        // TODO Auto-generated method stub
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

    @Override
    public CertificateModel findById(String id) {
        // TODO Auto-generated method stub
        CertificateModel certificate = null;
        
        GetRequest request = new GetRequest(INDEX, TYPE, id);
        GetResponse response = transportClient.get(request).actionGet();
        if(response.isExists()) {
            String data = response.getSourceAsString();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                certificate = objectMapper.readValue(data, CertificateModel.class);
                // id is stored as _id, so we need to grab it
                certificate.setId(UUID.fromString(response.getId()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return certificate;
    }

    @Override
    public CertificateListModel findAll(int offset, int limit) {
        System.out.println("ASINK: offset=" + Integer.toString(offset) + " limit=" + Integer.toString(limit) );
        SearchResponse response = transportClient.prepareSearch(INDEX).setTypes(TYPE).setFrom(offset).setSize(limit).get();
        return findRecords(response, offset, limit);
    }

    private CertificateListModel findRecords(SearchResponse response, int offset, int limit) {
        CertificateListModel certificateList = new CertificateListModel();
        List<CertificateModel> certificates = new ArrayList<CertificateModel>();
        if(response.getHits().totalHits > 0) {
            SearchHit[] hits = response.getHits().getHits();
            try {
                for(int ii = 0; ii < hits.length; ii++) {
                    CertificateModel certificate = objectMapper.readValue(hits[ii].getSourceAsString(), CertificateModel.class);
                    // id is stored as _id, we need to do a translation
                    certificate.setId(UUID.fromString(hits[ii].getId()));
                    certificates.add(certificate);
                }
                
                certificateList.setCertificates(certificates);
                certificateList.setCount(certificates.size());
                certificateList.setOffset(offset);
                certificateList.setLimit(limit);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        return certificateList;
    }

}
