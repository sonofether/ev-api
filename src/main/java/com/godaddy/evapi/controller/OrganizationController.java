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

import com.godaddy.evapi.model.OrganizationListModel;

@RestController
@RequestMapping(value = "/org")
public class OrganizationController {
    @GetMapping(value="", params = { "offset", "limit" })
    public boolean GetOrganizationList(@RequestParam( "offset" ) int offset, @RequestParam( "limit" ) int limit) {
        OrganizationListModel orgList = new OrganizationListModel();
        
        // Connect to datastore
        
        // For each result, create new OrganizationModel and add it to the list.
        
        // Set the count/index/etc
        orgList.setCount(0);
        orgList.setLimit(25);
        orgList.setOffset(0);
        
        return true;
    }
    
    @PostMapping(value="")
    public void AddOrganization() { // (@RequestBody OrganizationDTO signature)
        return;
    }
    
    // TODO: File upload logic???
    
    // This will only ever return ONE frecord
    @GetMapping(value="/{id}")
    public void GetOrganization(@PathVariable(value = "id") String orgId) {
        // Connect to data store
        
        // Get record
        
        // 404 if not found.
        
        // Return OrganizationModel
    }
    
    @DeleteMapping(value="/{id}")
    public void DeleteOrganization(@PathVariable(value = "id") String orgId) {
        // Connect to data store
        
        // Get record
       
        // Set status to removed
        
        // Update
        
        // Return
        return;
    }
    
    @PutMapping(value="/{id}")
    public void UpdateOrganization(@PathVariable(value = "id") String orgId) {
// Connect to data store
        
        // Get record
       
        // Uh?????
    }

    @GetMapping(value="/name/{name}", params = { "offset", "limit"})
    public void GetOrganizationByName(@PathVariable(value="name") String orgName, 
                @RequestParam( "offset" ) int offset, @RequestParam( "limit" ) int limit) {
        // Connect to data store
        
        // Get records
       
    }
    
    @GetMapping(value="/serial/{serialNumber}", params = { "offset", "limit"})
    public void GetOrganizationBySerialNumber(@PathVariable(value="serialNumber") String serialNumber, 
                @RequestParam( "offset" ) int offset, @RequestParam( "limit" ) int limit) {
        // Connect to data store
        
        // Get records
    }

    @GetMapping(value="/{name}/{serialNumber}/{country}", params = { "offset", "limit"})
    public void GetOrganizationByNameSerialNumberCountry(@PathVariable(value="name") String orgName, 
                @PathVariable(value="serialNumber") String serialNumber, @PathVariable(value="country") String country, 
                @RequestParam( "offset" ) int offset, @RequestParam( "limit" ) int limit) {
        // Connect to data store
        
        // Get records
    }
    
    @GetMapping(value="/{name}/{serialNumber}/{country}/{state}", params = { "offset", "limit"})
    public void GetOrganizationByNameSerialNumberCountryState(@PathVariable(value="name") String orgName, 
                @PathVariable(value="serialNumber") String serialNumber, @PathVariable(value="country") String country,
                @PathVariable(value="state") String state, 
                @RequestParam( "offset" ) int offset, @RequestParam( "limit" ) int limit) {
        // Connect to data store
        
        // Get records
    }

    
    // Resource Actions
    @GetMapping(value="/collisionDetect/{organizationName}")
    public void collisionDetectByOrganizationName(@PathVariable(value="name") String orgName) {
        
    }
    
    @GetMapping(value="/collisionDetect/serial/{serialNumber}")
    public void collisionDetectBySerialNumber(@PathVariable(value="serialNumber") String serialNumber) {
        
    }
    
    @GetMapping(value="/collisionDetect/{name}/{serialNumber}/{country}/{state}")
    public void collisionDetectByAll(@PathVariable(value="name") String orgName, 
                @PathVariable(value="serialNumber") String serialNumber, @PathVariable(value="country") String country,
                @PathVariable(value="state") String state) {
        
    }
    
    @GetMapping(value="/collisionDetect/{}")
    public void collision() {
        
    }
}
