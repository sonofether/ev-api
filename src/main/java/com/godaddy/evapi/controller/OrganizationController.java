package com.godaddy.evapi.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.godaddy.evapi.model.OrganizationListModel;
import com.godaddy.evapi.model.OrganizationModel;
import com.godaddy.evapi.service.IOrganizationService;

@RestController
@RequestMapping(value = "/org")
public class OrganizationController {
    @Autowired
    IOrganizationService organizationService;
    
    @GetMapping(value="")
    public ResponseEntity<OrganizationListModel> GetOrganizationList(@RequestParam( value="offset") Optional<Integer> offset, @RequestParam( value="limit") Optional<Integer> limit) {
        int os = offset.isPresent() ? offset.get() : 0;
        int lim = limit.isPresent() && limit.get() < 101 ? limit.get() : 25;
        OrganizationListModel orgList = organizationService.findAll(os, lim);
        if(orgList.getCount() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<OrganizationListModel>(orgList, HttpStatus.OK);
    }
    
    @PostMapping(value="")
    public void AddOrganization() { // (@RequestBody OrganizationDTO signature)
        return;
    }
    
    // TODO: File upload logic???
    
    // This will only ever return ONE record
    @GetMapping(value="/{id}")
    public ResponseEntity<OrganizationModel> GetOrganization(@PathVariable(value = "id") String orgId) {
        OrganizationModel org = organizationService.findById(orgId);        
        if(org == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<OrganizationModel>(org, HttpStatus.OK);
    }
    
    @DeleteMapping(value="/{id}")
    public void DeleteOrganization(@PathVariable(value = "id") String orgId) {
        // Make sure they own this record...
        
        // Connect to data store
        
        // Get record
       
        // Set status to removed
        
        // Update
        
        // Return
        return;
    }
    
    @PutMapping(value="/{id}")
    public void UpdateOrganization(@PathVariable(value = "id") String orgId) {
        // Make sure they own this record...
        
        // Connect to data store
        
        // Get record
       
        // Update the record with the new data - What do we want to lalow to be updated?
    }

    @GetMapping(value="/name/{name}")
    public ResponseEntity<OrganizationListModel> GetOrganizationByName(@PathVariable(value="name") String name, 
                @RequestParam( value="offset") Optional<Integer> offset, @RequestParam( value="limit") Optional<Integer> limit) {
        int os = offset.isPresent() ? offset.get() : 0;
        int lim = limit.isPresent() && limit.get() < 101 ? limit.get() : 25;
        OrganizationListModel orgList = organizationService.findByCommonName(name, os, lim);
        if(orgList.getCount() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<OrganizationListModel>(orgList, HttpStatus.OK);       
    }
    
    @GetMapping(value="/serial/{serialNumber}")
    public ResponseEntity<OrganizationListModel> GetOrganizationBySerialNumber(@PathVariable(value="serialNumber") String serialNumber, 
                @RequestParam( value="offset") Optional<Integer> offset, @RequestParam( value="limit") Optional<Integer> limit) {
        int os = offset.isPresent() ? offset.get() : 0;
        int lim = limit.isPresent() && limit.get() < 101 ? limit.get() : 25;
        OrganizationListModel orgList = organizationService.findBySerialNumber(serialNumber, os, lim);
        if(orgList.getCount() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<OrganizationListModel>(orgList, HttpStatus.OK);       
    }

    @GetMapping(value="/{name}/{serialNumber}/{country}")
    public void GetOrganizationByNameSerialNumberCountry(@PathVariable(value="name") String orgName, 
                @PathVariable(value="serialNumber") String serialNumber, @PathVariable(value="country") String country, 
                @RequestParam( value="offset") Optional<Integer> offset, @RequestParam( value="limit") Optional<Integer> limit) {
        // Connect to data store
        
        // Get records
    }
    
    @GetMapping(value="/{name}/{serialNumber}/{country}/{state}")
    public void GetOrganizationByNameSerialNumberCountryState(@PathVariable(value="name") String orgName, 
                @PathVariable(value="serialNumber") String serialNumber, @PathVariable(value="country") String country,
                @PathVariable(value="state") String state, 
                @RequestParam( value="offset") Optional<Integer> offset, @RequestParam( value="limit") Optional<Integer> limit) {
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
