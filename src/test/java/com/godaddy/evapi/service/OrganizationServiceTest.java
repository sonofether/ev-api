package com.godaddy.evapi.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.transport.TransportClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;

import com.godaddy.evapi.model.OrganizationModel;

//@RunWith(PowerMockRunner.class)
//@PrepareForTest({SearchHits.class, SearchHit.class})
public class OrganizationServiceTest {
    
    @Mock
    RestHighLevelClient restClient;
    
    @Mock
    DeleteRequestBuilder drb;
    
    @Mock
    GetResponse getResponse;
    
    @Mock
    ActionFuture<GetResponse> afGet;
    
    @Mock
    SearchRequestBuilder srb;
    
    @Mock
    SearchResponse searchResponse;
    
    @Mock
    DeleteResponse deleteResponse;
              
    @InjectMocks
    OrganizationService orgService;
    
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void saveTest() {
        //boolean result = orgService.save(new OrganizationModel());
        //assert(result);
    }
    
    @Test
    public void deleteTest() throws IOException {              
        when(deleteResponse.getResult()).thenReturn(DocWriteResponse.Result.DELETED);
        when(drb.get()).thenReturn(deleteResponse);
        boolean result = orgService.delete("testcode");
        //assert(result);
    }
    
    @Test
    public void findByIdTest() throws Exception {
        when(getResponse.isExists()).thenReturn(true);
        when(getResponse.getSourceAsString()).thenReturn(recordAsString());
        when(getResponse.getId()).thenReturn(UUID.randomUUID().toString());
        when(afGet.actionGet()).thenReturn(getResponse);
        //when(restClient.get( anyObject() )).thenReturn(getResponse);
        //OrganizationModel org = orgService.findById("1");
        //assertNotNull(org);
        //assert(org.getLocalityName().equals("Tempe"));
    }
    
    @Test
    public void findAllTest() {
        // Cannot mock searchhits because it is a final class
        // Not sure how to accomplish this at the moment
        /*
        SearchHits searchHits = Mockito.mock(SearchHits.class);
        SearchHit[] searchHitArray = new SearchHit[1];
        SearchHit searchHit = Mockito.mock(SearchHit.class);
        searchHitArray[0] = searchHit;
        when(searchHit.getId()).thenReturn(UUID.randomUUID().toString());
        when(searchHit.getSourceAsString()).thenReturn(recordAsString());
        when(searchHits.getHits()).thenReturn(searchHitArray);
        when(searchHits.totalHits).thenReturn((long) 1);
        when(searchResponse.getHits()).thenReturn(searchHits);
        when(srb.get()).thenReturn(searchResponse);
        when(srb.setSize(anyInt())).thenReturn(srb);
        when(srb.setFrom(anyInt())).thenReturn(srb);
        when(srb.setTypes(anyString())).thenReturn(srb);
        when(transportClient.prepareSearch(anyString())).thenReturn(srb);
        OrganizationListModel orgList = orgService.findAll(0, 25);
        assertNotNull(orgList);
        assert(orgList.getOrganizations().size() == 1);
        */
    }
    
    private String recordAsString() {
        return "{" + 
        "\"organizationName\": \"Asink Org\"," + 
        "\"commonName\": \"asink.com\"," + 
        "\"serialNumber\": \"123457\"," + 
        "\"localityName\": \"Tempe\"," + 
        "\"stateOrProvinceName\": \"AZ\"," + 
        "\"countryName\": \"US\"," + 
        "\"ca\": \"Asink Authority Inc\"" + 
        "}";
    }
    
}
