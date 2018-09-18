package com.godaddy.evapi.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.godaddy.evapi.model.CollisionModel;
import com.godaddy.evapi.model.OrganizationInputModel;
import com.godaddy.evapi.model.OrganizationListModel;
import com.godaddy.evapi.model.OrganizationModel;
import com.godaddy.evapi.service.IOrganizationService;

import io.jsonwebtoken.Claims;

@RestController
@RequestMapping(value = "/org")
public class OrganizationController {
    @Autowired
    IOrganizationService organizationService;
    
    private int offset;
    private int limit;
    
    @GetMapping(value="")
    public ResponseEntity<OrganizationListModel> GetOrganizationList(@RequestParam( value="offset") Optional<Integer> offset, @RequestParam( value="limit") Optional<Integer> limit) {
        setOffsetLimit(offset,limit);
        OrganizationListModel orgList = organizationService.findAll(this.offset, this.limit);
        if(orgList.getCount() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<OrganizationListModel>(orgList, HttpStatus.OK);
    }
    
    @PostMapping(value="")
    public ResponseEntity<String> AddOrganization(@RequestBody OrganizationInputModel organization) {
        boolean success = false;
        // Grab the auth token, convert to json, and get the ca value
        Claims token = (Claims)SecurityContextHolder.getContext().getAuthentication().getCredentials();
        // TODO: GET this once basic auth is setup properly 
        //String ca = "Adam's Certs Inc";
        String ca = (String)token.get("ca").toString();
        
        // Create our new id
        UUID id = UUID.randomUUID();
        // Validate fields.
        if(validateNewRecord(organization.getOrganizationName(), organization.getCommonName())) {
            // Setup the model to be stored                   
            OrganizationModel org = new OrganizationModel(id, organization.getOrganizationName(), organization.getCommonName(), organization.getSerialNumber(),
                        organization.getLocalityName(), organization.getStateOrProvinceName(), organization.getCountryName(), ca);
            boolean result = organizationService.save(org);
            
            if(result == true) {
                // TODO: Create certificate record
                // TODO: Write to the block chain
                //organization.getExpirationDate();
                //organization.getIssuedDate();
                
                success = true;
                return new ResponseEntity<String>(id.toString(), HttpStatus.CREATED);
            }
        }
        
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    
    // TODO: File upload logic???
    
    // This will only ever return ONE record - or at least it should
    @GetMapping(value="/{id}")
    public ResponseEntity<OrganizationModel> GetOrganization(@PathVariable(value = "id") String orgId) {
        OrganizationModel org = organizationService.findById(orgId);        
        if(org == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<OrganizationModel>(org, HttpStatus.OK);
    }

    // We do not support deletes.
    @DeleteMapping(value="/{id}")
    public ResponseEntity<HttpStatus> DeleteOrganization(@PathVariable(value = "id") String orgId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
        
    
    @PutMapping(value="/{id}")
    public ResponseEntity<HttpStatus> UpdateOrganization(@PathVariable(value = "id") String orgId) {
        String principal = (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
        // Make sure they own this record...
        
        // Get record data from the body
        
        // Connect to data store
        
        // Get record
       
        // Update the record with the new data - What do we want to lalow to be updated?
    }

    @GetMapping(value="/name/{name}")
    public ResponseEntity<OrganizationListModel> GetOrganizationByName(@PathVariable(value="name") String name, 
                @RequestParam( value="offset") Optional<Integer> offset, @RequestParam( value="limit") Optional<Integer> limit) {
        setOffsetLimit(offset,limit);
        OrganizationListModel orgList = organizationService.findByOrganizationName(name, this.offset, this.limit);
        if(orgList.getCount() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<OrganizationListModel>(orgList, HttpStatus.OK);       
    }
    
    @GetMapping(value="/commonname/{name}")
    public ResponseEntity<OrganizationListModel> GetOrganizationByCommonName(@PathVariable(value="name") String name, 
                @RequestParam( value="offset") Optional<Integer> offset, @RequestParam( value="limit") Optional<Integer> limit) {
        setOffsetLimit(offset,limit);
        OrganizationListModel orgList = organizationService.findByCommonName(name, this.offset, this.limit);
        if(orgList.getCount() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<OrganizationListModel>(orgList, HttpStatus.OK);       
    }
    
    @GetMapping(value="/serial/{serialNumber}")
    public ResponseEntity<OrganizationListModel> GetOrganizationBySerialNumber(@PathVariable(value="serialNumber") String serialNumber, 
                @RequestParam( value="offset") Optional<Integer> offset, @RequestParam( value="limit") Optional<Integer> limit) {
        setOffsetLimit(offset,limit);
        OrganizationListModel orgList = organizationService.findBySerialNumber(serialNumber, this.offset, this.limit);
        if(orgList.getCount() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<OrganizationListModel>(orgList, HttpStatus.OK);
    }

    @GetMapping(value="/{name}/{serialNumber}/{country}")
    public ResponseEntity<OrganizationListModel> GetOrganizationByNameSerialNumberCountry(@PathVariable(value="name") String name, 
                @PathVariable(value="serialNumber") String serialNumber, @PathVariable(value="country") String country, 
                @RequestParam( value="offset") Optional<Integer> offset, @RequestParam( value="limit") Optional<Integer> limit) {
        setOffsetLimit(offset,limit);
        OrganizationListModel orgList = organizationService.findByNameSerialNumberCountry(name, serialNumber, country,this.offset, this.limit);
        if(orgList.getCount() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<OrganizationListModel>(orgList, HttpStatus.OK);
    }
    
    @GetMapping(value="/{name}/{serialNumber}/{country}/{state}")
    public ResponseEntity<OrganizationListModel> GetOrganizationByNameSerialNumberCountryState(@PathVariable(value="name") String name, 
                @PathVariable(value="serialNumber") String serialNumber, @PathVariable(value="country") String country,
                @PathVariable(value="state") String state, 
                @RequestParam( value="offset") Optional<Integer> offset, @RequestParam( value="limit") Optional<Integer> limit) {
        setOffsetLimit(offset,limit);
        OrganizationListModel orgList = organizationService.findByNameSerialNumberCountryState(name, serialNumber, country, state, this.offset, this.limit);
        if(orgList.getCount() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<OrganizationListModel>(orgList, HttpStatus.OK);
    }
    
    // Resource Actions
    @GetMapping(value="/collisionDetect/{name}")
    public CollisionModel collisionDetectByCommonName(@PathVariable(value="name") String name) {
        CollisionModel collision = new CollisionModel();
        OrganizationListModel orgList = organizationService.findByOrganizationName(name, 0, 1);
        if(orgList.getCount() > 0) {
            collision.setCollision(true);
        }
        
        return collision;
    }
    
    @GetMapping(value="/collisionDetect/commonName/{commonName}")
    public CollisionModel collisionDetectByOrganizationName(@PathVariable(value="commonName") String commonName) {
        CollisionModel collision = new CollisionModel();
        OrganizationListModel orgList = organizationService.findByCommonName(commonName, 0, 1);
        if(orgList.getCount() > 0) {
            collision.setCollision(true);
        }
        
        return collision;
    }
    
    
    @GetMapping(value="/collisionDetect/serial/{serialNumber}")
    public CollisionModel collisionDetectBySerialNumber(@PathVariable(value="serialNumber") String serialNumber) {
        CollisionModel collision = new CollisionModel();
        OrganizationListModel orgList = organizationService.findBySerialNumber(serialNumber, 0, 1);
        if(orgList.getCount() > 0) {
            collision.setCollision(true);
        }
        
        return collision;        
    }
    
    @GetMapping(value="/collisionDetect/{name}/{serialNumber}/{country}")
    public CollisionModel collisionDetectByAll(@PathVariable(value="name") String name, 
                @PathVariable(value="serialNumber") String serialNumber, @PathVariable(value="country") String country
                ) {
        CollisionModel collision = new CollisionModel();
        OrganizationListModel orgList = organizationService.findByNameSerialNumberCountry(name, serialNumber, country, 0, 1);
        if(orgList.getCount() > 0) {
            collision.setCollision(true);
        }
        
        return collision;        
    }
    
    @GetMapping(value="/collisionDetect/{name}/{serialNumber}/{country}/{state}")
    public CollisionModel collisionDetectByAll(@PathVariable(value="name") String name, 
                @PathVariable(value="serialNumber") String serialNumber, @PathVariable(value="country") String country,
                @PathVariable(value="state") String state) {
        CollisionModel collision = new CollisionModel();
        OrganizationListModel orgList = organizationService.findByNameSerialNumberCountryState(name, serialNumber, country, state, 0, 1);
        if(orgList.getCount() > 0) {
            collision.setCollision(true);
        }
        
        return collision;        
    }
        
    // PRIVATE CALLS / HELPER FUNCTIONS
    
    // Validate/sanity check the offset and limit values
    private void setOffsetLimit(Optional<Integer> offset, Optional<Integer> limit) {
        // Offset must not be negative
        this.offset =  offset.isPresent() && offset.get() > 0 ? offset.get() : 0;
        // Limit must be between 1 and 100. If 0, we would not return anything. Negative is right out.
        this.limit = limit.isPresent() && limit.get() < 101 && limit.get() > 0 ? limit.get() : 25;
    }
    
    private boolean validateNewRecord(String name, String cName) {
        // TODO: We may need to be more granular when checking the organization name - Might need region.
        boolean result = false;
        OrganizationListModel orgList = organizationService.findByOrganizationName(name, this.offset, this.limit);
        if(orgList.getCount() < 1) {
            orgList = organizationService.findByCommonName(cName, 0, 1);
            if(orgList.getCount() < 1) {
                result = true;
            }
        }
        
        return result;        
    }
}
