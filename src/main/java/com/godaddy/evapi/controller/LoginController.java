package com.godaddy.evapi.controller;

import java.io.IOException;
import java.util.Date;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
@RequestMapping(value = "/login")
public class LoginController {    
    @GetMapping("")
    public boolean getLogin() {
        return true;
    }
    
    // This will never actually get hit. The request is handled by the authentication filter. We can just do some testing in here for now.
    @PostMapping("")
    public String authenticateUser(@RequestHeader("authorization") String authString, HttpServletResponse response) {       
        String decodedAuth = "";        
        return decodedAuth;
    }
}
