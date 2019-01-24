package com.godaddy.evapi.security;


import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.godaddy.evapi.ApplicationConfiguration;
import com.godaddy.evapi.service.BlockchainService;
import com.google.common.base.Optional;

// This doesn't seem to work... Not sure why...
//@RunWith(SpringJUnit4ClassRunner.class)
//@TestConfiguration
//@TestPropertySource(locations= {"classpath:application.properties"}, properties= {"auth.file.name=/Users/authfile","jwt.private.key.file=/Users/private_key.der","token.timeout.minutes=5"})
public class BasicAuthenticationProviderTest {  
    @InjectMocks
    private BasicAuthenticationProvider baProvider;
    
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        // Need to set these values manually since the properties calls above don't seem to work.
        ReflectionTestUtils.setField(baProvider, "fileName", "/Users/authfile");
        ReflectionTestUtils.setField(baProvider, "privateKeyFile", "/Users/private_key.der");
        ReflectionTestUtils.setField(baProvider, "timeout", 5);
        ReflectionTestUtils.setField(baProvider, "defaultSalt", "GettingSaltyUpInHere");
    }
    
    @Test
    public void basicProviderAuthenitcationTest() throws Exception {                
        String creds = "asink:password";        
        Authentication authMock = Mockito.mock(Authentication.class);
        Optional<String> value = Optional.fromNullable("Basic " + java.util.Base64.getMimeEncoder().encodeToString(creds.getBytes()));
        when(authMock.getPrincipal()).thenReturn(value);
        Authentication authorized = baProvider.authenticate(authMock);
        assert(authorized != null);
        assert(authorized.getPrincipal() != null);
    }
    
    @Test
    public void basicProviderAuthenitcationFailureTest() throws Exception {
        String creds = "asink:potato";        
        Authentication authMock = Mockito.mock(Authentication.class);
        Optional<String> value = Optional.fromNullable("Basic " + java.util.Base64.getMimeEncoder().encodeToString(creds.getBytes()));
        when(authMock.getPrincipal()).thenReturn(value);
        Authentication authorized = baProvider.authenticate(authMock);
        assert(authorized == null);
    }
    
    /*
    @Test
    public void buildAuthFile() {
        String user = OneWayEncryption.HashValue("asink", defaultSaltq.getBytes());
        byte[] salt = OneWayEncryption.getSalt();
        String password = OneWayEncryption.HashValue("password", salt); 
        
        System.out.println(fileName);
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        bw.write(user + "\t" + password + "\t" + java.util.Base64.getMimeEncoder().encodeToString(salt) + "\tAdam's Super Cool and Authorized CA");
        bw.close();
    }
    
    @Test
    public void interactWithBlockchain() {
        BlockchainService blocky = new BlockchainService();
        int length = blocky.getLength();
        System.out.println("ASINK****************");
        System.out.println(length);
        System.out.println("ASINK****************");
        assert(length == 1);
    }
    
    @Test
    public void interactWithBlockchain2() {
        BlockchainService blocky = new BlockchainService();
        String value = blocky.getRecord("123");
        System.out.println("ASINK****************");
        System.out.println(value);
        System.out.println("ASINK****************");
        assert(value.equals("asink2.com"));
    }
    
    @Test
    public void interactWithBlockchain3() {
        BlockchainService blocky = new BlockchainService();
        //String value = blocky.sendCurrency( "0xd3a68a6258df9625C77add6dEf200b87C7305c8c", BigInteger.valueOf(1), "{\"name\": \"asink\"}");
        System.out.println("ASINK****************");
        //System.out.println(value);
        System.out.println("ASINK****************");
    }
    
    @Test
    public void interactWithBlockchain4() {
        BlockchainService blocky = new BlockchainService();
        blocky.writeRecord("1234", "asink.com");
        System.out.println("ASINK****************");
        System.out.println("writeRecord");
        System.out.println("ASINK****************");
    }
    */
    
    @Configuration
    static class ApplicationConfiguration {

      @Bean
      public static PropertySourcesPlaceholderConfigurer propertiesResolver() {
          return new PropertySourcesPlaceholderConfigurer();
      }

    }
}
