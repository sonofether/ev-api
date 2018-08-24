package com.godaddy.evapi.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        
        // Get list from data store / 
        // Check for our entry
        
        // If any entries, return true, otherwise false.
        return message;
    }
}