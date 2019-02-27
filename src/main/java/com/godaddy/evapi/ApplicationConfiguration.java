package com.godaddy.evapi;

import java.net.InetAddress;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.http.AWSRequestSigningApacheInterceptor;

// Load the properties file so we can use it.
@Configuration
@PropertySource("classpath:application.properties")
public class ApplicationConfiguration {
    @Value("${elasticsearch.cluster.name}")
    private String clusterName;
    
    @Value("${elasticsearch.cluster.host}")
    private String host;
    
    @Value("${elasticsearch.cluster.port}")
    private int port;
    
    @Value("${aws.es.endpoint}")
    protected String endpoint;
   
    @Value("${aws.es.servicename}")
    protected String serviceName;
    
    @Value("${aws.es.region}")
    protected String region;

    @Bean(destroyMethod="close")
    public RestHighLevelClient restClient() throws Exception {
        HttpRequestInterceptor interceptor = new AWSRequestSigningApacheInterceptor(getServiceName(), generateSigner(), new DefaultAWSCredentialsProviderChain());
        return new RestHighLevelClient(RestClient.builder(HttpHost.create(getEndpoint())).setHttpClientConfigCallback(hacb -> hacb.addInterceptorLast(interceptor)));
    }
    
    @SuppressWarnings("resource")
    @Bean(destroyMethod="close")
    public TransportClient transportClient() throws Exception {
        Settings settings = Settings.builder().put("cluster.name", clusterName).build();
        return new PreBuiltTransportClient(settings).addTransportAddress(new TransportAddress(InetAddress.getByName(host), port));
    }
    
    private String getEndpoint() {
        return endpoint;
    }
    
    private String getServiceName() {
        return serviceName;
    }
    
    private AWS4Signer generateSigner() {
        AWS4Signer signer = new AWS4Signer();
        signer.setServiceName(serviceName);
        signer.setRegionName(region);        
        return signer;
    }   
}
