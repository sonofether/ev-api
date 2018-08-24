package com.godaddy.evapi.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/validation")
public class ValidationController {
    @GetMapping(value="", params = { "offset", "limit" })
    public void GetValidationList(@RequestParam( "offset" ) int offset, @RequestParam( "limit" ) int limit) {
        // Connect to datastore
        
        // Get records
        
        // Build list.
        
        // Set count/offset/limit 
        
        // Return
    }
    
    @GetMapping("/{id}")
    public void GetValidationById(@PathVariable(value="id") String id) {
        // Connect to datastore
        
        // Get single record
        
        // Build ValidationItemModel
        
        // Return        
    }
    
    @GetMapping(value="/certificate/{certId}", params = { "offset", "limit" })
    public void AddValidationItem(@PathVariable(value="certId") String certId,
                @RequestParam( "offset" ) int offset, @RequestParam( "limit" ) int limit) {
        
    }
    
    // TODO: Handle file upload
    @PostMapping("")
    public void AddValidationItem() {
        // Connect to data store
        
        // Create new object
        // Generate guid id
        // Set object data
        
        // Save record
        
        // return id
    }
    
    @PutMapping("/{id}")
    public void UpdateRecord() {
        // Connect to datastore
        
        // Get record
        
        // Update record
        
        // return
    }
    
    @DeleteMapping("/{id}")
    public void RemoveRecord() {
        // Connect to datastore
        
        // Get record
        
        // Set status to Removed
        
        // return
    }
    
}
