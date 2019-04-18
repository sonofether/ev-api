package com.godaddy.evapi.security;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.base.Optional;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokenAuthenticationProviderTest {
    @InjectMocks
    private TokenAuthenticationProvider tokenProvider;
    
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        // Need to set these values manually since the properties calls above don't seem to work.
        ReflectionTestUtils.setField(tokenProvider, "publicKeyFile", "/Users/public_key.der");
    }
    
    @Test
    public void tokenProviderSupportsTest() {
        boolean result = tokenProvider.supports(PreAuthenticatedAuthenticationToken.class);
        assert(result);
    }
    
    @Test
    public void tokenProviderSupportsFailureTest() {
        boolean result = tokenProvider.supports(Object.class);
        assert(result == false);
    }
    
    // This test checks for a valid JWT
    @Test
    public void tokenProviderAuthorizeTest() {
        Authentication authMock = Mockito.mock(Authentication.class);
        // Needs to provide a valid token...
        Optional<String> value = Optional.fromNullable(generateToken(5));
        when(authMock.getPrincipal()).thenReturn(value);
        Authentication auth = tokenProvider.authenticate(authMock);
        assert(auth != null);
    }
    
    @Test
    public void tokenProviderAuthorizeFailureTest() {
        Authentication authMock = Mockito.mock(Authentication.class);
        // Needs to provide a valid token...
        Optional<String> value = Optional.absent();
        when(authMock.getPrincipal()).thenReturn(value);
        Authentication auth = tokenProvider.authenticate(authMock);
        assert(auth != null);
    }
    
    // This test checks the timeout logic.
    @Test
    public void tokenProviderFailureTest() throws Exception{
        Authentication authMock = Mockito.mock(Authentication.class);
        // Needs to provide a valid token...
        Optional<String> value = Optional.fromNullable(generateToken(0));
        when(authMock.getPrincipal()).thenReturn(value);
        Thread.sleep(1000);
        Authentication auth = tokenProvider.authenticate(authMock);
        assert(auth == null);                
    }
    
    // This test checks for a valid JWT
    @Test
    public void tokenProviderAuthorizeTimeoutTest() {
        Authentication authMock = Mockito.mock(Authentication.class);
        // Needs to provide a valid token...
        Optional<String> value = Optional.fromNullable(generateExpiredToken());
        when(authMock.getPrincipal()).thenReturn(value);
        Authentication auth = tokenProvider.authenticate(authMock);
        assert(auth == null);
    }
    
    public static String generateToken(int timeout) {
        try {
            byte[] privateBytes = Files.readAllBytes(Paths.get("/Users/private_key.der"));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privateBytes);
            RSAPrivateKey key = (RSAPrivateKey)keyFactory.generatePrivate(privSpec);
            Date now = new Date();
            // Generate the jwt
            // Use default values for issuer/user/ca
            String token = Jwts.builder().setIssuer("TestIssuance").setSubject("evapi auth")
                        .claim("user", "asink").claim("ca", "Test CA")
                        .setIssuedAt(now).setExpiration(DateUtils.addMinutes(now, timeout))
                        .signWith(SignatureAlgorithm.RS256, key).compact();
            
            return token;
        } catch (Exception ex) {
            ex.printStackTrace();
        } 
        return null;
    }
    
    public static String generateExpiredToken() {
        try {
            byte[] privateBytes = Files.readAllBytes(Paths.get("/Users/private_key.der"));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privateBytes);
            RSAPrivateKey key = (RSAPrivateKey)keyFactory.generatePrivate(privSpec);
            Date now = new Date();
            // Generate the jwt
            // Use default values for issuer/user/ca
            String token = Jwts.builder().setIssuer("TestIssuance").setSubject("evapi auth")
                        .claim("user", "asink").claim("ca", "Test CA")
                        .setIssuedAt(now).setExpiration(DateUtils.addMinutes(now, -2))
                        .signWith(SignatureAlgorithm.RS256, key).compact();
            
            return token;
        } catch (Exception ex) {
            ex.printStackTrace();
        } 
        return null;
    }
}
