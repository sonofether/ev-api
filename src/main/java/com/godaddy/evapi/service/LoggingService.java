package com.godaddy.evapi.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.evapi.model.LogModel;

@Service
public class LoggingService implements ILoggingService {
    @Autowired
    RestHighLevelClient restClient;
    
    static final String INDEX = "logging";
    static final String TYPE = "record";
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public boolean insertLog(LogModel logEntry) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String index = INDEX + format.format( new Date() );
        boolean result = false;
        
        try {
            GetIndexRequest existsRequest = new GetIndexRequest().indices(index); 
            if( !restClient.indices().exists(existsRequest, RequestOptions.DEFAULT) ) {
                // Create index
                createIndex(index);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }

        // We need to take out id, since ES stores it as _id. Would duplicate the data.
        Map data = objectMapper.convertValue(logEntry, Map.class);
        data.remove("id");
        try {
            IndexRequest request = new IndexRequest(index, TYPE, logEntry.getId().toString()).source(data);
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
    public LogModel fetchLog(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LogModel fetchLogs(Date startTime, Date endTime) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LogModel fetchLogsByCA(String ca, Date startTime, Date endTime) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LogModel fetchLogsByEndpoint(String endpoint, Date startTime, Date endTime) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LogModel fetchLogsByEnpdointAndOperation(String endpoint, String operation, Date startTime, Date endTime) {
        // TODO Auto-generated method stub
        return null;
    }

    
    // PRIVATE/HELPER FUNCTIONS
    
    private void createIndex(String index) throws Exception {
        CreateIndexRequest createRequest = new CreateIndexRequest(index);
        
        Map<String, Object> indexMap = LogModel.createIndex();
        
        createRequest.mapping("record", indexMap);
        
        restClient.indices().create(createRequest, RequestOptions.DEFAULT);
    }
}
