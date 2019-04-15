package com.godaddy.evapi.service;

import java.io.IOException;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.evapi.model.CertificateListModel;
import com.godaddy.evapi.model.CertificateModel;

@Service
public class CertificateService extends BaseAWSService implements ICertificateService {

    @Autowired
    RestHighLevelClient restClient;
    
    static final String INDEX = "certificate";
    static final String TYPE = "record";
    
    private ObjectMapper objectMapper = new ObjectMapper(); 
    
    @Override
    public boolean save(CertificateModel certificate) {
        boolean result = false;
        // We need to take out id, since ES stores it as _id. Would duplicate the data.
        Map data = objectMapper.convertValue(certificate, Map.class);
        data.remove("id");
        IndexRequest request = new IndexRequest(INDEX, TYPE, certificate.getId().toString()).source(data);
        IndexResponse response;
        try {
            response = restClient.index(request);
            if(response.getResult() == DocWriteResponse.Result.CREATED || 
               response.getResult() == DocWriteResponse.Result.UPDATED) {
                result = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean delete(String id) {
        DeleteRequest deleteRequest = new DeleteRequest(INDEX, TYPE, id);
        DeleteResponse response;
        try {
            response = restClient.delete(deleteRequest);
            if(response != null && response.getResult() == DocWriteResponse.Result.DELETED) {
                // Hey, it worked!
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public CertificateModel findById(String id) {
        CertificateModel certificate = null;

        try {
            GetRequest request = new GetRequest(INDEX, TYPE, id);
            GetResponse response = restClient.get(request);
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return certificate;
    }

    @Override
    public CertificateListModel findAll(int offset, int limit) {
        try {
            SearchRequest request = generateSearchRequest(QueryBuilders.matchAllQuery(), offset, limit, INDEX, TYPE);
            SearchResponse response = restClient.search(request);
            return findRecords(response, offset, limit);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return null;
    }
    
    // PRIVATE FUNCTION CALLS / HELPERS

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
