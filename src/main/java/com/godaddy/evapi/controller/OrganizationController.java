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

import com.godaddy.evapi.model.BlacklistListModel;
import com.godaddy.evapi.model.CertificateModel;
import com.godaddy.evapi.model.CollisionModel;
import com.godaddy.evapi.model.IdModel;
import com.godaddy.evapi.model.OrganizationInputModel;
import com.godaddy.evapi.model.OrganizationListModel;
import com.godaddy.evapi.model.OrganizationModel;
import com.godaddy.evapi.service.ICertificateService;
import com.godaddy.evapi.service.IOrganizationService;

import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/org")
@EnableHypermediaSupport(type = { EnableHypermediaSupport.HypermediaType.HAL })
@Api(value = "Organization", description = "Resource for getting and modifying organization entries")
public class OrganizationController extends BaseController {
    @Autowired
    IOrganizationService organizationService;
    
    //@Autowired
    //ICertificateService certificateService;
        
    private String countryDisplayName;
    private String countryCode;
    
    @GetMapping(value="")
    @ApiOperation(value = "Gets all organization records, paginated", response = OrganizationListModel.class)
    public ResponseEntity<Resource<OrganizationListModel>> GetOrganizationList(HttpServletRequest request,
                @RequestParam( value="offset") Optional<Integer> offsetValue,
                @RequestParam( value="limit") Optional<Integer> limitValue) {
        setOffsetLimit(offsetValue,limitValue);
        OrganizationListModel orgList = organizationService.findAll(this.offset, this.limit);
        if(orgList.getCount() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }        
        
        Resource<OrganizationListModel> resource = new Resource<>(orgList, generateLinks(request, this.offset, this.limit, orgList.getCount()));
        return ResponseEntity.ok(resource);
    }
    
    @PostMapping(value="")
    @ApiOperation(value = "Create a new organization record", response = IdModel.class)
    public ResponseEntity<IdModel> AddOrganization(@ApiParam(name="organization", value="Organization data to add") @RequestBody OrganizationInputModel organization) {
        boolean success = false;
        String ca = getCAName();
        
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
                return new ResponseEntity<IdModel>(new IdModel(id.toString()), HttpStatus.CREATED);
            }
        }
        
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    
    // This will only ever return ONE record - or at least it should
    @GetMapping(value="/{id}")
    @ApiOperation(value = "Get an organization record by id", response = OrganizationModel.class)
    public ResponseEntity<OrganizationModel> GetOrganization(@ApiParam(name="id", value="Record id") @PathVariable(value = "id") String orgId) {
        OrganizationModel org = organizationService.findById(orgId);        
        if(org == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<OrganizationModel>(org, HttpStatus.OK);
    }

    // We do not support deletes.
    @DeleteMapping(value="/{id}")
    @ApiOperation(value = "Delete an organization record by id, currently not implemented", response = HttpStatus.class)
    public ResponseEntity<HttpStatus> DeleteOrganization(@ApiParam(name="id", value="Record id") @PathVariable(value = "id") String orgId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
        
    
    @PutMapping(value="/{id}")
    @ApiOperation(value = "Udate an organization record by id, currently not implemented", response = HttpStatus.class)
    public ResponseEntity<HttpStatus> UpdateOrganization(@ApiParam(name="id", value="Record id") @PathVariable(value = "id") String orgId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @GetMapping(value="/name/{name}")
    @ApiOperation(value = "Get all organization records matching an organization name", response = OrganizationListModel.class)
    public ResponseEntity<Resource<OrganizationListModel>> GetOrganizationByName(HttpServletRequest request, 
                @ApiParam(name="name", value="Organization Name to search") @PathVariable(value="name") String name, 
                @RequestParam( value="offset") Optional<Integer> offsetValue, @RequestParam( value="limit") Optional<Integer> limitValue) {
        setOffsetLimit(offsetValue,limitValue);
        OrganizationListModel orgList = organizationService.findByOrganizationName(name, this.offset, this.limit);
        if(orgList.getCount() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        Resource<OrganizationListModel> resource = new Resource<>(orgList, generateLinks(request, this.offset, this.limit, orgList.getCount()));
        return ResponseEntity.ok(resource);
    }
    
    @GetMapping(value="/commonname/{cname}")
    @ApiOperation(value = "Get all organization records matching a cName", response = OrganizationListModel.class)
    public ResponseEntity<Resource<OrganizationListModel>> GetOrganizationByCommonName(HttpServletRequest request, 
                @ApiParam(name="cname", value="cName to search") @PathVariable(value="cname") String name, 
                @RequestParam( value="offset") Optional<Integer> offsetValue, @RequestParam( value="limit") Optional<Integer> limitValue) {
        setOffsetLimit(offsetValue,limitValue);
        OrganizationListModel orgList = organizationService.findByCommonName(name, this.offset, this.limit);
        if(orgList.getCount() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        Resource<OrganizationListModel> resource = new Resource<>(orgList, generateLinks(request, this.offset, this.limit, orgList.getCount()));
        return ResponseEntity.ok(resource);
    }
    
    @GetMapping(value="/serial/{serialNumber}")
    @ApiOperation(value = "Get all organization records matching a serial number", response = OrganizationListModel.class)
    public ResponseEntity<Resource<OrganizationListModel>> GetOrganizationBySerialNumber(HttpServletRequest request, 
                @ApiParam(name="serialNumber", value="Serial Number to search") @PathVariable(value="serialNumber") String serialNumber, 
                @RequestParam( value="offset") Optional<Integer> offsetValue, @RequestParam( value="limit") Optional<Integer> limitValue) {
        setOffsetLimit(offsetValue,limitValue);
        OrganizationListModel orgList = organizationService.findBySerialNumber(serialNumber, this.offset, this.limit);
        if(orgList.getCount() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        Resource<OrganizationListModel> resource = new Resource<>(orgList, generateLinks(request, this.offset, this.limit, orgList.getCount()));
        return ResponseEntity.ok(resource);
    }

    @GetMapping(value="/{name}/{serialNumber}/{country}")
    @ApiOperation(value = "Get all organization records matching an organization name, serial number, and country", response = OrganizationListModel.class)
    public ResponseEntity<Resource<OrganizationListModel>> GetOrganizationByNameSerialNumberCountry(HttpServletRequest request, 
                @ApiParam(name="name", value="Organization Name to search") @PathVariable(value="name") String name, 
                @ApiParam(name="serialNumber", value="Serial Number to search") @PathVariable(value="serialNumber") String serialNumber,
                @ApiParam(name="country", value="Country to search") @PathVariable(value="country") String country, 
                @RequestParam( value="offset") Optional<Integer> offsetValue, @RequestParam( value="limit") Optional<Integer> limitValue) {
        setOffsetLimit(offsetValue,limitValue);
        OrganizationListModel orgList = organizationService.findByNameSerialNumberCountry(name, serialNumber, country,this.offset, this.limit);
        if(orgList.getCount() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Resource<OrganizationListModel> resource = new Resource<>(orgList, generateLinks(request, this.offset, this.limit, orgList.getCount()));
        return ResponseEntity.ok(resource);
    }
    
    @GetMapping(value="/{name}/{serialNumber}/{country}/{state}")
    @ApiOperation(value = "Get all organization records matching an organization name, serial number, country, and state", response = OrganizationListModel.class)
    public ResponseEntity<Resource<OrganizationListModel>> GetOrganizationByNameSerialNumberCountryState(HttpServletRequest request, 
                @ApiParam(name="name", value="Organization Name to search") @PathVariable(value="name") String name, 
                @ApiParam(name="serialNumber", value="Serial Number to search") @PathVariable(value="serialNumber") String serialNumber,
                @ApiParam(name="country", value="Country to search") @PathVariable(value="country") String country,
                @ApiParam(name="state", value="State to search") @PathVariable(value="state") String state, 
                @RequestParam( value="offset") Optional<Integer> offsetValue, @RequestParam( value="limit") Optional<Integer> limitValue) {
        setOffsetLimit(offsetValue,limitValue);
        OrganizationListModel orgList = organizationService.findByNameSerialNumberCountryState(name, serialNumber, country, state, this.offset, this.limit);
        if(orgList.getCount() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        Resource<OrganizationListModel> resource = new Resource<>(orgList, generateLinks(request, this.offset, this.limit, orgList.getCount()));
        return ResponseEntity.ok(resource);
    }
    
    // Resource Actions
    @GetMapping(value="/collisionDetect/{name}")
    @ApiOperation(value = "True/False if a match is found for the supplied organization name", response = CollisionModel.class)
    public CollisionModel CollisionDetectByOrganizationName(@ApiParam(name="name", value="Organization Name to search") @PathVariable(value="name") String name) {
        CollisionModel collision = new CollisionModel();
        OrganizationListModel orgList = organizationService.findByOrganizationName(name, 0, 1);
        if(orgList.getCount() > 0) {
            collision.setCollision(true);
        }
        
        return collision;
    }
    
    @GetMapping(value="/collisionDetect/commonName/{commonName}")
    @ApiOperation(value = "True/False if a match is found for the supplied cName", response = CollisionModel.class)
    public CollisionModel CollisionDetectByCommonName(@ApiParam(name="commonName", value="Common Name to search")@PathVariable(value="commonName") String commonName) {
        CollisionModel collision = new CollisionModel();
        OrganizationListModel orgList = organizationService.findByCommonName(commonName, 0, 1);
        if(orgList.getCount() > 0) {
            collision.setCollision(true);
        }
        
        return collision;
    }
    
    
    @GetMapping(value="/collisionDetect/serial/{serialNumber}")
    @ApiOperation(value = "True/False if a match is found for the supplied serial number", response = CollisionModel.class)
    public CollisionModel CollisionDetectBySerialNumber(@ApiParam(name="serialNumber", value="Serial Number to search") @PathVariable(value="serialNumber") String serialNumber) {
        CollisionModel collision = new CollisionModel();
        OrganizationListModel orgList = organizationService.findBySerialNumber(serialNumber, 0, 1);
        if(orgList.getCount() > 0) {
            collision.setCollision(true);
        }
        
        return collision;        
    }
    
    @GetMapping(value="/collisionDetect/{name}/{serialNumber}/{country}")
    @ApiOperation(value = "True/False if a match is found for the supplied organization name, serial number, and country", response = CollisionModel.class)
    public CollisionModel CollisionDetectByAll(@ApiParam(name="name", value="Organization Name to search") @PathVariable(value="name") String name, 
                @ApiParam(name="serialNumber", value="Serial Number to search") @PathVariable(value="serialNumber") String serialNumber,
                @ApiParam(name="country", value="Country to search") @PathVariable(value="country") String country
                ) {
        CollisionModel collision = new CollisionModel();
        OrganizationListModel orgList = organizationService.findByNameSerialNumberCountry(name, serialNumber, country, 0, 1);
        if(orgList.getCount() > 0) {
            collision.setCollision(true);
        }
        
        return collision;        
    }
    
    @GetMapping(value="/collisionDetect/{name}/{serialNumber}/{country}/{state}")
    @ApiOperation(value = "True/False if a match is found for the supplied organization name, serial number, country, and state", response = CollisionModel.class)
    public CollisionModel CollisionDetectByAll(@ApiParam(name="name", value="Organization Name to search") @PathVariable(value="name") String name, 
                @ApiParam(name="serialNumber", value="Serial Number to search") @PathVariable(value="serialNumber") String serialNumber,
                @ApiParam(name="country", value="Country to search") @PathVariable(value="country") String country,
                @ApiParam(name="state", value="State to search") @PathVariable(value="state") String state) {
        CollisionModel collision = new CollisionModel();
        OrganizationListModel orgList = organizationService.findByNameSerialNumberCountryState(name, serialNumber, country, state, 0, 1);
        if(orgList.getCount() > 0) {
            collision.setCollision(true);
        }
        
        return collision;        
    }
        
    // PRIVATE CALLS / HELPER FUNCTIONS
        
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
}
