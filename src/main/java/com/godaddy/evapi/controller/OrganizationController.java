package com.godaddy.evapi.controller;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.config.EnableHypermediaSupport;
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

import com.godaddy.evapi.model.CertificateModel;
import com.godaddy.evapi.model.CollisionModel;
import com.godaddy.evapi.model.OrganizationInputModel;
import com.godaddy.evapi.model.OrganizationListModel;
import com.godaddy.evapi.model.OrganizationModel;
import com.godaddy.evapi.service.ICertificateService;
import com.godaddy.evapi.service.IOrganizationService;

import io.jsonwebtoken.Claims;

@RestController
@RequestMapping(value = "/org")
@EnableHypermediaSupport(type = { EnableHypermediaSupport.HypermediaType.HAL })
public class OrganizationController {
    @Autowired
    IOrganizationService organizationService;
    
    //@Autowired
    //ICertificateService certificateService;
    
    private int offset;
    private int limit;
    
    private String countryDisplayName;
    private String countryCode;
    
    @GetMapping(value="")
    public ResponseEntity<Resource<OrganizationListModel>> GetOrganizationList(HttpServletRequest request,
                @RequestParam( value="offset") Optional<Integer> offset,
                @RequestParam( value="limit") Optional<Integer> limit) {
        setOffsetLimit(offset,limit);
        OrganizationListModel orgList = organizationService.findAll(this.offset, this.limit);
        if(orgList.getCount() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }        
        
        Resource<OrganizationListModel> resource = new Resource<>(orgList, generateLinks(request, this.offset, this.limit, orgList.getCount()));
        return ResponseEntity.ok(resource);
    }
    
    @PostMapping(value="")
    public ResponseEntity<String> AddOrganization(@RequestBody OrganizationInputModel organization) {
        boolean success = false;
        // Grab the auth token, convert to json, and get the ca value
        Claims token = (Claims)SecurityContextHolder.getContext().getAuthentication().getCredentials();
        String ca = (String)token.get("ca").toString();
        
        // Create our new id
        UUID id = UUID.randomUUID();
        // Validate fields.
        if(validateNewRecord(organization.getOrganizationName(), organization.getCommonName())) {
            // Setup the model to be stored                   
            OrganizationModel org = new OrganizationModel(id, organization.getOrganizationName(), organization.getCommonName(), organization.getSerialNumber(),
                        organization.getLocalityName(), organization.getStateOrProvinceName(), organization.getCountryName(), ca);
            if(organizationService.save(org)) {
                // Create certificate record
                CertificateModel cert = new CertificateModel(org.getId(), org.getId(), organization.getOrganizationName(), ca, organization.getCommonName(), 
                            organization.getExpirationDate(), organization.getIssuedDate(), 1, "0" );
                //certificateService.save(cert);
                
                // TODO: Write to the block chain
                
                success = true;
                return new ResponseEntity<String>("{\"id\": \"" + id.toString() + "\"}", HttpStatus.CREATED);
            }
        }
        
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    
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
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @GetMapping(value="/name/{name}")
    public ResponseEntity<Resource<OrganizationListModel>> GetOrganizationByName(HttpServletRequest request, 
                @PathVariable(value="name") String name, 
                @RequestParam( value="offset") Optional<Integer> offset, @RequestParam( value="limit") Optional<Integer> limit) {
        setOffsetLimit(offset,limit);
        OrganizationListModel orgList = organizationService.findByOrganizationName(name, this.offset, this.limit);
        if(orgList.getCount() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        Resource<OrganizationListModel> resource = new Resource<>(orgList, generateLinks(request, this.offset, this.limit, orgList.getCount()));
        return ResponseEntity.ok(resource);
    }
    
    @GetMapping(value="/commonname/{name}")
    public ResponseEntity<Resource<OrganizationListModel>> GetOrganizationByCommonName(HttpServletRequest request, 
                @PathVariable(value="name") String name, 
                @RequestParam( value="offset") Optional<Integer> offset, @RequestParam( value="limit") Optional<Integer> limit) {
        setOffsetLimit(offset,limit);
        OrganizationListModel orgList = organizationService.findByCommonName(name, this.offset, this.limit);
        if(orgList.getCount() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        Resource<OrganizationListModel> resource = new Resource<>(orgList, generateLinks(request, this.offset, this.limit, orgList.getCount()));
        return ResponseEntity.ok(resource);
    }
    
    @GetMapping(value="/serial/{serialNumber}")
    public ResponseEntity<Resource<OrganizationListModel>> GetOrganizationBySerialNumber(HttpServletRequest request, 
                @PathVariable(value="serialNumber") String serialNumber, 
                @RequestParam( value="offset") Optional<Integer> offset, @RequestParam( value="limit") Optional<Integer> limit) {
        setOffsetLimit(offset,limit);
        OrganizationListModel orgList = organizationService.findBySerialNumber(serialNumber, this.offset, this.limit);
        if(orgList.getCount() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        Resource<OrganizationListModel> resource = new Resource<>(orgList, generateLinks(request, this.offset, this.limit, orgList.getCount()));
        return ResponseEntity.ok(resource);
    }

    @GetMapping(value="/{name}/{serialNumber}/{country}")
    public ResponseEntity<Resource<OrganizationListModel>> GetOrganizationByNameSerialNumberCountry(HttpServletRequest request, 
                @PathVariable(value="name") String name, 
                @PathVariable(value="serialNumber") String serialNumber, @PathVariable(value="country") String country, 
                @RequestParam( value="offset") Optional<Integer> offset, @RequestParam( value="limit") Optional<Integer> limit) {
        setOffsetLimit(offset,limit);
        OrganizationListModel orgList = organizationService.findByNameSerialNumberCountry(name, serialNumber, country,this.offset, this.limit);
        if(orgList.getCount() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Resource<OrganizationListModel> resource = new Resource<>(orgList, generateLinks(request, this.offset, this.limit, orgList.getCount()));
        return ResponseEntity.ok(resource);
    }
    
    @GetMapping(value="/{name}/{serialNumber}/{country}/{state}")
    public ResponseEntity<Resource<OrganizationListModel>> GetOrganizationByNameSerialNumberCountryState(HttpServletRequest request, 
                @PathVariable(value="name") String name, 
                @PathVariable(value="serialNumber") String serialNumber, @PathVariable(value="country") String country,
                @PathVariable(value="state") String state, 
                @RequestParam( value="offset") Optional<Integer> offset, @RequestParam( value="limit") Optional<Integer> limit) {
        setOffsetLimit(offset,limit);
        OrganizationListModel orgList = organizationService.findByNameSerialNumberCountryState(name, serialNumber, country, state, this.offset, this.limit);
        if(orgList.getCount() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        Resource<OrganizationListModel> resource = new Resource<>(orgList, generateLinks(request, this.offset, this.limit, orgList.getCount()));
        return ResponseEntity.ok(resource);
    }
    
    // Resource Actions
    @GetMapping(value="/collisionDetect/{name}")
    public CollisionModel CollisionDetectByOrganizationName(@PathVariable(value="name") String name) {
        CollisionModel collision = new CollisionModel();
        OrganizationListModel orgList = organizationService.findByOrganizationName(name, 0, 1);
        if(orgList.getCount() > 0) {
            collision.setCollision(true);
        }
        
        return collision;
    }
    
    @GetMapping(value="/collisionDetect/commonName/{commonName}")
    public CollisionModel CollisionDetectByCommonName(@PathVariable(value="commonName") String commonName) {
        CollisionModel collision = new CollisionModel();
        OrganizationListModel orgList = organizationService.findByCommonName(commonName, 0, 1);
        if(orgList.getCount() > 0) {
            collision.setCollision(true);
        }
        
        return collision;
    }
    
    
    @GetMapping(value="/collisionDetect/serial/{serialNumber}")
    public CollisionModel CollisionDetectBySerialNumber(@PathVariable(value="serialNumber") String serialNumber) {
        CollisionModel collision = new CollisionModel();
        OrganizationListModel orgList = organizationService.findBySerialNumber(serialNumber, 0, 1);
        if(orgList.getCount() > 0) {
            collision.setCollision(true);
        }
        
        return collision;        
    }
    
    @GetMapping(value="/collisionDetect/{name}/{serialNumber}/{country}")
    public CollisionModel CollisionDetectByAll(@PathVariable(value="name") String name, 
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
    public CollisionModel CollisionDetectByAll(@PathVariable(value="name") String name, 
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
    
    private boolean validateCountry(String country) {
        boolean isValid = false;
        countryDisplayName = "";
        countryCode = "";
        // Try looking up by code
        try {
            Locale locale = new Locale("", country);
            if(locale != null) {
                countryCode = country;
                countryDisplayName = locale.getDisplayCountry();
                isValid = true;
            }
        } catch (Exception ex) {
            // Didn't match. No big deal. Try lookup by name.
        }
        
        // Try looking up by name
        if(!isValid) {
            Map<String,Locale> map = new HashMap<String,Locale>();
            for (Locale locale : Locale.getAvailableLocales()) {
                map.put(locale.getDisplayCountry().toLowerCase(), locale);
            }
            
            Locale locale = map.get(country.toLowerCase());
            if(locale != null) {
                isValid = true;
                countryDisplayName = locale.getDisplayCountry();
                countryCode = locale.getCountry();
            }
        }
        
        return isValid;
    }
    
    private boolean validateorganizationName(String orgName) {
        boolean isValid = false;
        
        return isValid;
    }
    
    private List<Link> generateLinks(HttpServletRequest request, int offset, int limit, int size) {
        List<Link> links = new ArrayList<Link>();
        String self = request.getRequestURL().toString() + (request.getQueryString() != null && request.getQueryString().length() > 0 ? "?" + request.getQueryString() : "" );
        links.add(new Link(self).withRel("self"));
        links.add(new Link(request.getRequestURL().toString() + "?offset=0&limit=" + limit).withRel("first"));
        
        if(offset != 0) {
            int prevOffset = (offset - limit) > 0 ? offset - limit : 0;
            Link link = new Link(request.getRequestURL().toString() + "?offset=" + prevOffset + "&limit=" + limit).withRel("prev");
            links.add(link);
        }
        
        if(size >= limit) {
            int nextOffset = offset + size;
            Link link = new Link(request.getRequestURL().toString() + "?offset=" + nextOffset + "&limit=" + limit).withRel("next");
            links.add(link);
        }
        
        return links;
    }
}
