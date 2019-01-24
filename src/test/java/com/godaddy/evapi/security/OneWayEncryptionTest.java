package com.godaddy.evapi.security;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

public class OneWayEncryptionTest {
    @InjectMocks
    private TokenAuthenticationProvider tokenProvider;
    
    @InjectMocks
    private OneWayEncryption owEncrypt;
    
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        // Need to set these values manually since the properties calls above don't seem to work.
        ReflectionTestUtils.setField(tokenProvider, "publicKeyFile", "/Users/public_key.der");
        owEncrypt.setDefaultSalt("GettingSaltyUpInHere");
    }
    
    @Test
    public void encryptionTest() throws Exception {
        String value = "Test Value";
        byte[] salt = owEncrypt.getSalt();
        String result = owEncrypt.HashValue(value, salt);
        assert(!result.equals(value));
        assert(result != null);
        assert(result.length() > 0);
    }
    
    @Test
    public void encryptionTest2() throws Exception {
        String value = "Test Value";
        String result = owEncrypt.HashValue(value, null);
        assert(!result.equals(value));
        assert(result != null);
        assert(result.length() > 0);
    }
}
