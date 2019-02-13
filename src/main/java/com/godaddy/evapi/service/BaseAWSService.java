package com.godaddy.evapi.service;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;

public class BaseAWSService {    
    protected SearchRequest generateSearchRequest(QueryBuilder query, int offset, int limit, String index, String type) {
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);
        searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
        
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(query);
        searchSourceBuilder.size(limit);
        searchSourceBuilder.from(offset);
        searchSourceBuilder.explain(true);

        searchRequest.source(searchSourceBuilder);
        return searchRequest;
    }

}
