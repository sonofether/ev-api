package com.godaddy.evapi.service;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
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
    
    protected QueryBuilder buildQueryFromFilters(String filter) {
        if(filter.length() > 0) {
            int searchTerms = 0;
            BoolQueryBuilder query = QueryBuilders.boolQuery();
            String[] filterTerms = filter.split("and|AND");
            for(String filterTerm : filterTerms) {
                String[] parts = filterTerm.trim().split(" ");
                if(parts.length < 3) {
                    continue;
                }
    
                String fieldName = parts[0].trim();
                String searchTerm = parts[2].trim();
                if(fieldName.length() < 1 || searchTerm.length() < 1) {
                    continue;
                }
                
                QueryBuilder matchQuery = QueryBuilders.matchQuery(fieldName, searchTerm);
                String equality = parts[1].trim().toLowerCase();
                if(equality.equals("eq")) {
                    ++searchTerms;
                    query = query.must(matchQuery);
                } else if (equality.equals("neq")) {
                    ++searchTerms;
                    query = query.mustNot(matchQuery);
                }
            }
            
            if(++searchTerms > 0) {
                return query;
            }
        }
        
        return QueryBuilders.matchAllQuery();
    }

}
