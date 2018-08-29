package com.godaddy.evapi.security;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.google.common.base.Optional;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class BasicAuthenticationProvider implements AuthenticationProvider {
    @Value( "${jwt.private.key.file}" )
    private String privateKeyFile;
    
    @Value("${token.timeout.minutes}")
    private int timeout;
    
    private String user = "";
    private String pass = "";
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Optional<String> authHeader = (Optional) authentication.getPrincipal();
        if (!authHeader.isPresent()) {
            throw new BadCredentialsException("Invalid Domain User Credentials");
        }
        
        String basicAuth = authHeader.get();
        // Get creds from basic auth
        if(!AuthenticateUser(basicAuth)) {
            return null;
        }
                
        String token = this.GenerateToken();
        if(token == null || token.length() < 1) {
            return null;
        }
        
        // Setup auth and add the jwt
        PreAuthenticatedAuthenticationToken tokenAuth = new PreAuthenticatedAuthenticationToken(user, token);
        tokenAuth.setAuthenticated(true);
        
        return tokenAuth;
    }
    
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private boolean AuthenticateUser(String basicAuth) {
        // Header is in the format "Basic Base64EncodedString"
        // We need to extract data before decoding it back to original string
        try {
            String[] authParts = basicAuth.split("\\s+");
            String authInfo = authParts[1];
            // Decode the data back to original string
            byte[] bytes = java.util.Base64.getMimeDecoder().decode(authInfo);
            String decodedAuth = new String(bytes);
            StringTokenizer tokenizer = new StringTokenizer(decodedAuth, ":");
            user = tokenizer.nextToken();
            pass = tokenizer.nextToken();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        
        // TODO: Validate the decoded values against a data store
        // TODO: Figure out level of auth

        // TODO: Return true if auth succeeds, false otherwise
        
        return true;
    }
    
    private String GenerateToken() {
        try {
            byte[] privateBytes = Files.readAllBytes(Paths.get(privateKeyFile));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privateBytes);
            RSAPrivateKey key = (RSAPrivateKey)keyFactory.generatePrivate(privSpec);
            Date now = new Date();
            // Generate the jwt
            String token = Jwts.builder().setIssuer("TestIssuance").setSubject("evapi auth").claim("user", user)
                        .setIssuedAt(now).setExpiration(DateUtils.addMinutes(now, timeout))
                        .signWith(SignatureAlgorithm.RS256, key).compact();
            
            return token;
        } catch (IOException iex) {
            iex.printStackTrace();
        } catch (NoSuchAlgorithmException nsaex) {
            nsaex.printStackTrace();
        } catch (InvalidKeySpecException iksex) {
            iksex.printStackTrace();
        }
        return null;
    }

}
