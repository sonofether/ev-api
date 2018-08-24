package com.godaddy.evapi;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

// Load the properties file so we can use it.
@Configuration
@PropertySource("classpath:application.properties")
public class ApplicationConfiguration {

}
