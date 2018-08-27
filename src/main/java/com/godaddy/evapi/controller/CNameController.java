package com.godaddy.evapi.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Claims;

@RestController
@RequestMapping(value = "/cname")
public class CNameController {
    @Value( "${system.message:ThisIsADefaultTest}" )
    private String message;
    
    @GetMapping("/flaglist/{cname}")
    public String CheckBlacklistByCName(@PathVariable(value="cname") String cName) {
        
        // TODO: Once we decide on a datastore and query mechanism, build all this out.
        if(cName == null || cName.length() < 3) {
            // Return false - no match
        }
        
        // TODO: Get list from data store / 
        // TODO: Check for our entry
        
        // TODO: For now output this so we can validate stuff
        Claims claims = (Claims)SecurityContextHolder.getContext().getAuthentication().getCredentials();
        message = claims.getSubject() + " " + claims.getExpiration().toString();
        
        
        // If any entries, return true, otherwise false.
        return message;
    }
}