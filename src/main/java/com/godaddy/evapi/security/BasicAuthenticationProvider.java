package com.godaddy.evapi.security;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.google.common.base.Optional;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class BasicAuthenticationProvider implements AuthenticationProvider {
    private final static Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    
    @Value( "${jwt.private.key.file}" )
    private String privateKeyFile;
    
    @Value("${token.timeout.minutes}")
    private int timeout;
    
    @Value("${auth.file.name}")
    private String fileName;
    
    @Value( "${encryption.salt.default}" )
    private String defaultSalt;
    
    private String user = "";
    private String pass = "";
    private String ca = "";
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Optional<String> authHeader = (Optional) authentication.getPrincipal();
        if (!authHeader.isPresent()) {
            throw new BadCredentialsException("Invalid Domain User Credentials");
        }
        
        String basicAuth = authHeader.get();
        // Get creds from basic auth
        if(!AuthenticateUser(basicAuth)) {
            logger.debug("Failed to authenticate user");
            return null;
        }
                
        String token = this.GenerateToken();
        if(token == null || token.length() < 1) {
            logger.debug("Failed to generate token");
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
    
    // Private and helper functions
    private boolean AuthenticateUser(String basicAuth) {
          boolean isValid = false;
        // Header is in the format "Basic Base64EncodedString"
        // We need to extract data before decoding it back to original string
        try {
            String[] authParts = basicAuth.split("\\s+");
            String authInfo = authParts[1];
            // Decode the data back to original string
            byte[] bytes = java.util.Base64.getMimeDecoder().decode(authInfo);
            String decodedAuth = new String(bytes);
            StringTokenizer tokenizer = new StringTokenizer(decodedAuth, ":");
            user = tokenizer.nextToken().trim();
            pass = tokenizer.nextToken().trim();
        } catch (Exception ex) {
            logger.debug("Failed to get basic auth header: " + ex.getMessage());
            ex.printStackTrace();
            return isValid;
        }
        
        // Validate the decoded values against the stored values
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(fileName));
            String line = null;
            String hashedUser = OneWayEncryption.HashValue(user, defaultSalt.getBytes());
            do {
                line = buffer.readLine();
                if(line != null) {
                    String[] values = line.split("\t");
                    if(hashedUser.equals(values[0].trim())) {
                        String encryptedPassword = OneWayEncryption.HashValue(pass, java.util.Base64.getMimeDecoder().decode(values[2].trim()));
                        isValid = encryptedPassword.equals(values[1].trim());
                        ca = values[3].trim();
                        break;
                    }
                }
            } while (line != null);
            buffer.close();
        } catch (Exception ex) {
            logger.debug("Failed to compare against authfile: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        return isValid;
    }
        
    private String GenerateToken() {
        try {
            byte[] privateBytes = Files.readAllBytes(Paths.get(privateKeyFile));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privateBytes);
            RSAPrivateKey key = (RSAPrivateKey)keyFactory.generatePrivate(privSpec);
            Date now = new Date();
            // Generate the jwt
            String token = Jwts.builder().setIssuer("TestIssuance").setSubject("evapi auth")
                        .claim("user", user).claim("ca", ca)
                        .setIssuedAt(now).setExpiration(DateUtils.addMinutes(now, timeout))
                        .signWith(SignatureAlgorithm.RS256, key).compact();
            
            return token;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
