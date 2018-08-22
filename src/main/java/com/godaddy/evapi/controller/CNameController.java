package com.godaddy.evapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/cname")
public class CNameController {
    
    
    @GetMapping("/flaglist/{cname}")
    public void CheckBlacklistByCName(@PathVariable(value="cname") String cName) {
        if(cName == null || cName.length() < 3) {
            // Return flse - no match
        }
        
        // Get list from data store / 
        // Check for our entry
        
        // If any entries, return true, otherwise false.
        return;
    }
}