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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.google.common.base.Optional;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class TokenAuthenticationProvider implements AuthenticationProvider {
    @Value( "${jwt.public.key.file}" )
    private String publicKeyFile;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {        
        Optional<String> token = (Optional) authentication.getPrincipal();
        if (!token.isPresent() || token.get().isEmpty()) {
            throw new BadCredentialsException("Invalid token");
        }
        
        // Decode and validate token
        try {
            // Get our file
            byte[] content = Files.readAllBytes(Paths.get(publicKeyFile));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec spec = new X509EncodedKeySpec(content);
            RSAPublicKey publicKey = (RSAPublicKey)keyFactory.generatePublic(spec);
            // As long as this does not exception, we pass validation.
            Claims claims = Jwts.parser().setSigningKey(publicKey)
                    .parseClaimsJws(token.get()).getBody();
            // Check expires stamp and reject/redirect if expired.
            if (claims.getExpiration().before(new Date())) {
                return null;
            }
            
            // Setup auth
            PreAuthenticatedAuthenticationToken tokenAuth =  new PreAuthenticatedAuthenticationToken(token, claims);
            tokenAuth.setAuthenticated(true);
            
            return tokenAuth;
        } catch (NoSuchAlgorithmException nsaex) {
            nsaex.printStackTrace();
        } catch (InvalidKeySpecException iksex) {
            iksex.printStackTrace();
        } catch (IOException iex) {
            iex.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException oobex) {
            oobex.printStackTrace();
        }
        
        return null;
    }
        
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(PreAuthenticatedAuthenticationToken.class);
    }

}
