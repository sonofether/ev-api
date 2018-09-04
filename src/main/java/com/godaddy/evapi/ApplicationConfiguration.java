package com.godaddy.evapi;

import java.net.InetAddress;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

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
    
    @Bean(destroyMethod="close")
    public TransportClient transportClient() throws Exception {
        Settings settings = Settings.builder().put("cluster.name", clusterName).build();
        return new PreBuiltTransportClient(settings).addTransportAddress(new TransportAddress(InetAddress.getByName(host), port));
    }
}
