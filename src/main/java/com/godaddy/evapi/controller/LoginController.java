package com.godaddy.evapi.controller;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;

@RestController
@RequestMapping(value = "/login")
public class LoginController {
    @GetMapping("")
    public boolean getLogin() {
        return true;
    }
    
    // TODO: We need to add a filter for the token validation.
    @PostMapping("")
    public String authenticateUser(@RequestHeader("authorization") String authString, HttpServletResponse response) {
        // Get creds from basic auth
        String decodedAuth = "";
        // Header is in the format "Basic 5tyc0uiDat4"
        // We need to extract data before decoding it back to original string
        String[] authParts = authString.split("\\s+");
        String authInfo = authParts[1];
        // Decode the data back to original string
        byte[] bytes = null;
        bytes = java.util.Base64.getMimeDecoder().decode(authInfo);
        decodedAuth = new String(bytes);
        
        // TODO: alidate - Will this be checking against a DB, or...?
        
        // Encode as JWT
        JwtBuilder builder = Jwts.builder();
        // TODO: Make this configurable
        builder.setExpiration(new Date());
        
        String jwtStr = builder.compact();
        // Sign with our cert and then call copact.
        //builder.signWith(
        //            SignatureAlgorithm.HS256,
        //            secretService.getHS256SecretBytes()
        //)
        // Set X-Auth-Token header
        response.setHeader("X-Auth-Token", jwtStr);
        //return new JwtResponse(jws);        
        
        // Return to client
        
        return decodedAuth;
    }
}
