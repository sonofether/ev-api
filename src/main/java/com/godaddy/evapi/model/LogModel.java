package com.godaddy.evapi.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.client.RequestOptions;

public class LogModel {
    private UUID id;
    private String ip; 
    private String operation; // GET. PUT, DELETE, POST 
    private String endpoint;
    private String arguments;
    private String ca; // The calling ca
    private String result;
    private int offset;
    private int count;
    private int limit;
    private int code;
    private Date submitted;
    
    public LogModel(String ip, String operation, String endpoint, String args, String ca, String result, int offset, int count, int limit, int code) {
        id = UUID.randomUUID();
        this.ip = ip;
        this.operation = operation;
        this.endpoint = endpoint;
        this.arguments = args;
        this.ca = ca;
        this.result = result;
        this.offset = offset;
        this.count = count;
        this.limit = limit;
        this.code = code;
        this.submitted = new Date();
    }
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }
    
    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getOperation() {
        return operation;
    }
    
    public void setOperation(String operation) {
        this.operation = operation;
    }
    
    public String getEndpoint() {
        return endpoint;
    }
    
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
    
    public String getArguments() {
        return arguments;
    }
    
    public void setArguments(String arguments) {
        this.arguments = arguments;
    }
    
    public String getCa() {
        return ca;
    }
    
    public void setCa(String ca) {
        this.ca = ca;
    }
    
    public int getOffset() {
        return offset;
    }
    
    public void setOffset(int offset) {
        this.offset = offset;
    }
    
    public int getCount() {
        return count;
    }
    
    public void setCount(int count) {
        this.count = count;
    }
    
    public int getLimit() {
        return limit;
    }
    
    public void setLimit(int limit) {
        this.limit = limit;
    }
    
    public Date getSubmitted() {
        return submitted;
    }
    
    public void setSubmitted(Date submitted) {
        this.submitted = submitted;
    }

    public String getResult() {
        return result;
    }
    
    public void setResult(String result) {
        this.result = result;
    }
    
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
   
    public static Map<String,Object> createIndex() throws Exception {
        Map<String, Object> keywordType = new HashMap<>();
        keywordType.put("type", "keyword");
        Map<String, Object> shortType = new HashMap<>();
        shortType.put("type", "short");
        Map<String, Object> dateType = new HashMap<>();
        dateType.put("type", "date");
        Map<String, Object> properties = new HashMap<>();
        properties.put("ip", keywordType);
        properties.put("operation", keywordType);
        properties.put("endpoint", keywordType);
        properties.put("arguments", keywordType);
        properties.put("ca", keywordType);
        properties.put("offset", shortType);
        properties.put("limit", shortType);
        properties.put("count", shortType);
        properties.put("submitted", dateType);
        Map<String, Object> record = new HashMap<>();
        record.put("properties", properties);
        Map<String, Object> mappings = new HashMap<>();
        mappings.put("record", record);
        
        return mappings;
    }
}
