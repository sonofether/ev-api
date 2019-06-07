package com.godaddy.evapi.controller;


import java.io.UnsupportedEncodingException;
import java.net.IDN;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.godaddy.evapi.legalentity.ILegalEntity;
import com.godaddy.evapi.legalentity.LegalEntityFactory;
import com.godaddy.evapi.model.CertificateModel;
import com.godaddy.evapi.model.CollisionModel;
import com.godaddy.evapi.model.IdModel;
import com.godaddy.evapi.model.LogModel;
import com.godaddy.evapi.model.OrganizationInputModel;
import com.godaddy.evapi.model.OrganizationListModel;
import com.godaddy.evapi.model.OrganizationModel;
import com.godaddy.evapi.service.HomoglyphService;
import com.godaddy.evapi.service.ICertificateService;
import com.godaddy.evapi.service.ILoggingService;
import com.godaddy.evapi.service.IOrganizationService;
import com.google.common.collect.Sets;
import com.ibm.icu.text.SpoofChecker;
import com.ibm.icu.text.SpoofChecker.CheckResult;

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
    
    @Autowired
    ILoggingService loggingService;
    
    @Autowired
    HttpServletRequest request;
    
    @Autowired
    HomoglyphService homoglyphService;
    
    //@Autowired
    //ICertificateService certificateService;
        
    private String countryDisplayName;
    private String countryCode;
    
    @GetMapping(value="")
    @ApiOperation(value = "Gets all organization records, paginated", response = OrganizationListModel.class)
    public ResponseEntity<Resource<OrganizationListModel>> GetOrganizationList(
                @RequestParam( value="offset") Optional<Integer> offsetValue,
                @RequestParam( value="limit") Optional<Integer> limitValue, 
                @RequestParam( value="filters", defaultValue="") String filters) {
        setOffsetLimit(offsetValue,limitValue);
        OrganizationListModel orgList = organizationService.findByVariableArguments(filters, this.offset, this.limit);
        if(orgList == null || orgList.getCount() < 1) {
            loggingService.insertLog( new LogModel(request.getRemoteHost(), "GET", "/org/", "", getCAName(), "NOT_FOUND", this.offset, 0, this.limit, 404) );
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }        
        
        Resource<OrganizationListModel> resource = new Resource<>(orgList, generateLinks(request, this.offset, this.limit, orgList.getCount()));
        loggingService.insertLog( new LogModel(request.getRemoteHost(), "GET", "/org/", filters, getCAName(), "OK", this.offset, orgList.getCount(), this.limit, 200) );
        return ResponseEntity.ok(resource);
    }
    
    @PostMapping(value="")
    @ApiOperation(value = "Create a new organization record", response = IdModel.class)
    public ResponseEntity<IdModel> AddOrganization(@ApiParam(name="organization", value="Organization data to add", required = true) @RequestBody OrganizationInputModel organization) {
        String ca = getCAName();
        
        // Create our new id
        UUID id = UUID.randomUUID();
        // Validate fields.
        if(validateNewRecord(organization)) {
            // Setup the model to be stored                   
            OrganizationModel org = new OrganizationModel(id, organization.getOrganizationName(), organization.getCommonName(), organization.getSerialNumber(),
                        organization.getLocalityName(), organization.getStateOrProvinceName(), organization.getCountryName(), ca, organization.getPhoneNumber(),
                        organization.getAddress());
            if(organizationService.save(org)) {
                // Create certificate record
                CertificateModel cert = new CertificateModel(org.getId(), org.getId(), organization.getOrganizationName(), ca, organization.getCommonName(), 
                            organization.getExpirationDate(), organization.getIssuedDate(), 1, "0" );
                //certificateService.save(cert);
                
                // TODO: Write to the block chain
                
                loggingService.insertLog( new LogModel(request.getRemoteHost(), "POST", "/org/", id.toString(), ca, "CREATED", 0, 1, 0, 201) );
                return new ResponseEntity<IdModel>(new IdModel(id.toString()), HttpStatus.CREATED);
            }
        }
        
        loggingService.insertLog( new LogModel(request.getRemoteHost(), "POST", "/org/", id.toString(), getCAName(), "BAD_REQUEST", 0, 0, 0, 400) );
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    
    // This will only ever return ONE record - or at least it should
    @GetMapping(value="/{id}")
    @ApiOperation(value = "Get an organization record by id", response = OrganizationModel.class)
    public ResponseEntity<OrganizationModel> GetOrganization(@ApiParam(name="id", value="Record id", required = true) @PathVariable(value = "id") String orgId) {
        OrganizationModel org = organizationService.findById(orgId);        
        if(org == null) {
            loggingService.insertLog( new LogModel(request.getRemoteHost(), "GET", "/org/" + orgId, "", getCAName(), "NOT_FOUND", 0, 0, 0, 404) );
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        loggingService.insertLog( new LogModel(request.getRemoteHost(), "GET", "/org/" + orgId, "", getCAName(), "Ok", 0, 1, 0, 200) );
        return new ResponseEntity<OrganizationModel>(org, HttpStatus.OK);
    }

    // We do not support deletes.
    @DeleteMapping(value="/{id}")
    @ApiOperation(value = "Delete an organization record by id, currently not implemented", response = HttpStatus.class)
    public ResponseEntity<HttpStatus> DeleteOrganization(@ApiParam(name="id", value="Record id", required = true) @PathVariable(value = "id") String orgId) {
        loggingService.insertLog( new LogModel(request.getRemoteHost(), "DELETE", "/org/" + orgId, "", getCAName(), "NOT_IMPLEMENTED", 0, 1, 0, 501) );
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
        
    
    @PutMapping(value="/{id}")
    @ApiOperation(value = "Udate an organization record by id, currently not implemented", response = HttpStatus.class)
    public ResponseEntity<HttpStatus> UpdateOrganization(@ApiParam(name="id", value="Record id", required = true) @PathVariable(value = "id") String orgId) {
        loggingService.insertLog( new LogModel(request.getRemoteHost(), "PUT", "/org/" + orgId, "", getCAName(), "NOT_IMPLEMENTED", 0, 1, 0, 501) );
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @GetMapping(value="/name/{name}")
    @ApiOperation(value = "Get all organization records matching an organization name", response = OrganizationListModel.class)
    public ResponseEntity<Resource<OrganizationListModel>> GetOrganizationByName(
                @ApiParam(name="name", value="Organization Name to search", required = true) @PathVariable(value="name") String name, 
                @RequestParam( value="offset") Optional<Integer> offsetValue, @RequestParam( value="limit") Optional<Integer> limitValue) {
        setOffsetLimit(offsetValue,limitValue);
        OrganizationListModel orgList = organizationService.findByOrganizationName(name, this.offset, this.limit);
        if(orgList == null || orgList.getCount() < 1) {
            loggingService.insertLog( new LogModel(request.getRemoteHost(), "GET", "/org/name/" + name, "", getCAName(), "NOT_FOUND", 0, 0, 0, 404) );
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        Resource<OrganizationListModel> resource = new Resource<>(orgList, generateLinks(request, this.offset, this.limit, orgList.getCount()));
        return ResponseEntity.ok(resource);
    }
    
    @GetMapping(value="/commonName/{cname}")
    @ApiOperation(value = "Get all organization records matching a cName", response = OrganizationListModel.class)
    public ResponseEntity<Resource<OrganizationListModel>> GetOrganizationByCommonName(
                @ApiParam(name="cname", value="cName to search", required = true) @PathVariable(value="cname") String name, 
                @RequestParam( value="offset") Optional<Integer> offsetValue, @RequestParam( value="limit") Optional<Integer> limitValue) {
        setOffsetLimit(offsetValue,limitValue);
        OrganizationListModel orgList = organizationService.findByCommonName(name, this.offset, this.limit);
        if(orgList == null || orgList.getCount() < 1) {
            loggingService.insertLog( new LogModel(request.getRemoteHost(), "GET", "/org/commonname/" + name, "", getCAName(), "NOT_FOUND", 0, 0, 0, 404) );
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        Resource<OrganizationListModel> resource = new Resource<>(orgList, generateLinks(request, this.offset, this.limit, orgList.getCount()));
        return ResponseEntity.ok(resource);
    }
    
    @GetMapping(value="/serial/{serialNumber}")
    @ApiOperation(value = "Get all organization records matching a serial number", response = OrganizationListModel.class)
    public ResponseEntity<Resource<OrganizationListModel>> GetOrganizationBySerialNumber(
                @ApiParam(name="serialNumber", value="Serial Number to search", required = true) @PathVariable(value="serialNumber") String serialNumber, 
                @RequestParam( value="offset") Optional<Integer> offsetValue, @RequestParam( value="limit") Optional<Integer> limitValue) {
        setOffsetLimit(offsetValue,limitValue);
        OrganizationListModel orgList = organizationService.findBySerialNumber(serialNumber, this.offset, this.limit);
        if(orgList == null || orgList.getCount() < 1) {
            loggingService.insertLog( new LogModel(request.getRemoteHost(), "GET", "/org/serial" + serialNumber, "", getCAName(), "NOT_FOUND", 0, 0, 0, 404) );
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        Resource<OrganizationListModel> resource = new Resource<>(orgList, generateLinks(request, this.offset, this.limit, orgList.getCount()));
        return ResponseEntity.ok(resource);
    }

    @GetMapping(value="/{name}/{serialNumber}/{country}")
    @ApiOperation(value = "Get all organization records matching an organization name, serial number, and country", response = OrganizationListModel.class)
    public ResponseEntity<Resource<OrganizationListModel>> GetOrganizationByNameSerialNumberCountry(
                @ApiParam(name="name", value="Organization Name to search", required = true) @PathVariable(value="name") String name, 
                @ApiParam(name="serialNumber", value="Serial Number to search", required = true) @PathVariable(value="serialNumber") String serialNumber,
                @ApiParam(name="country", value="Country to search", required = true) @PathVariable(value="country") String country, 
                @RequestParam( value="offset") Optional<Integer> offsetValue, @RequestParam( value="limit") Optional<Integer> limitValue) {
        setOffsetLimit(offsetValue,limitValue);
        OrganizationListModel orgList = organizationService.findByNameSerialNumberCountry(name, serialNumber, country,this.offset, this.limit);
        if(orgList == null || orgList.getCount() < 1) {
            loggingService.insertLog( new LogModel(request.getRemoteHost(), "GET", "/org/" + name + "/" + serialNumber + "/" + country, "", getCAName(), "NOT_FOUND", 0, 0, 0, 404) );
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Resource<OrganizationListModel> resource = new Resource<>(orgList, generateLinks(request, this.offset, this.limit, orgList.getCount()));
        return ResponseEntity.ok(resource);
    }
    
    @GetMapping(value="/{name}/{serialNumber}/{country}/{state}")
    @ApiOperation(value = "Get all organization records matching an organization name, serial number, country, and state", response = OrganizationListModel.class)
    public ResponseEntity<Resource<OrganizationListModel>> GetOrganizationByNameSerialNumberCountryState(
                @ApiParam(name="name", value="Organization Name to search", required = true) @PathVariable(value="name") String name, 
                @ApiParam(name="serialNumber", value="Serial Number to search", required = true) @PathVariable(value="serialNumber") String serialNumber,
                @ApiParam(name="country", value="Country to search", required = true) @PathVariable(value="country") String country,
                @ApiParam(name="state", value="State to search", required = true) @PathVariable(value="state") String state, 
                @RequestParam( value="offset") Optional<Integer> offsetValue, @RequestParam( value="limit") Optional<Integer> limitValue) {
        setOffsetLimit(offsetValue,limitValue);
        OrganizationListModel orgList = organizationService.findByNameSerialNumberCountryState(name, serialNumber, country, state, this.offset, this.limit);
        if(orgList == null || orgList.getCount() < 1) {
            loggingService.insertLog( new LogModel(request.getRemoteHost(), "GET", "/org/" + name + "/" + serialNumber + "/" + country + "/" + state, "", getCAName(), "NOT_FOUND", 0, 0, 0, 404) );
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        Resource<OrganizationListModel> resource = new Resource<>(orgList, generateLinks(request, this.offset, this.limit, orgList.getCount()));
        return ResponseEntity.ok(resource);
    }
    
    // Resource Actions
    @GetMapping(value="/collisionDetect/{name}")
    @ApiOperation(value = "True/False if a match is found for the supplied organization name", response = CollisionModel.class)
    public CollisionModel CollisionDetectByOrganizationName(@ApiParam(name="name", value="Organization Name to search", required = true) @PathVariable(value="name") String name) {
        CollisionModel collision = new CollisionModel();
        OrganizationListModel orgList = organizationService.findByOrganizationName(name, 0, 1);
        if(orgList == null || orgList.getCount() > 0) {
            collision.setCollision(true);
        }
        
        loggingService.insertLog( new LogModel(request.getRemoteHost(), "GET", "/org/collisionDetect/" + name, "", getCAName(), "OK", 0, collision.isCollision() ? 1 : 0, 0, 200) );
        
        return collision;
    }
    
    @GetMapping(value="/collisionDetect/commonName/{commonName}")
    @ApiOperation(value = "True/False if a match is found for the supplied cName", response = CollisionModel.class)
    public CollisionModel CollisionDetectByCommonName(@ApiParam(name="commonName", value="Common Name to search", required = true) @PathVariable(value="commonName") String commonName) {
        CollisionModel collision = new CollisionModel();
        OrganizationListModel orgList = organizationService.findByCommonName(commonName, 0, 1);
        if(orgList == null || orgList.getCount() > 0) {
            collision.setCollision(true);
        }
        
        loggingService.insertLog( new LogModel(request.getRemoteHost(), "GET", "/org/collisionDetect/commonName/" + commonName, "", getCAName(), "OK", 0, collision.isCollision() ? 1 : 0, 0, 200) );
        
        return collision;
    }
    
    
    @GetMapping(value="/collisionDetect/serial/{serialNumber}")
    @ApiOperation(value = "True/False if a match is found for the supplied serial number", response = CollisionModel.class)
    public CollisionModel CollisionDetectBySerialNumber(@ApiParam(name="serialNumber", value="Serial Number to search", required = true) @PathVariable(value="serialNumber") String serialNumber) {
        CollisionModel collision = new CollisionModel();
        OrganizationListModel orgList = organizationService.findBySerialNumber(serialNumber, 0, 1);
        if(orgList == null || orgList.getCount() > 0) {
            collision.setCollision(true);
        }
        
        loggingService.insertLog( new LogModel(request.getRemoteHost(), "GET", "/org/collisionDetect/serial/" + serialNumber, "", getCAName(), "OK", 0, collision.isCollision() ? 1 : 0, 0, 200) );
        return collision;        
    }
    
    @GetMapping(value="/collisionDetect/{name}/{serialNumber}/{country}")
    @ApiOperation(value = "True/False if a match is found for the supplied organization name, serial number, and country", response = CollisionModel.class)
    public CollisionModel CollisionDetectByNameSerialCountry(@ApiParam(name="name", value="Organization Name to search", required = true) @PathVariable(value="name") String name, 
                @ApiParam(name="serialNumber", value="Serial Number to search", required = true) @PathVariable(value="serialNumber") String serialNumber,
                @ApiParam(name="country", value="Country to search", required = true) @PathVariable(value="country") String country
                ) {
        CollisionModel collision = new CollisionModel();
        OrganizationListModel orgList = organizationService.findByNameSerialNumberCountry(name, serialNumber, country, 0, 1);
        if(orgList != null && orgList.getCount() > 0) {
            collision.setCollision(true);
        }
        
        loggingService.insertLog( new LogModel(request.getRemoteHost(), "GET", "/org/collisionDetect/" + name + "/" + serialNumber + "/" + country, "", getCAName(), "OK", 0, collision.isCollision() ? 1 : 0, 0, 200) );
        return collision;        
    }
    
    @GetMapping(value="/collisionDetect/{name}/{serialNumber}/{country}/{state}")
    @ApiOperation(value = "True/False if a match is found for the supplied organization name, serial number, country, and state", response = CollisionModel.class)
    public CollisionModel CollisionDetectByAll(@ApiParam(name="name", value="Organization Name to search", required = true) @PathVariable(value="name") String name, 
                @ApiParam(name="serialNumber", value="Serial Number to search", required = true) @PathVariable(value="serialNumber") String serialNumber,
                @ApiParam(name="country", value="Country to search", required = true) @PathVariable(value="country") String country,
                @ApiParam(name="state", value="State to search", required = true) @PathVariable(value="state") String state) {
        CollisionModel collision = new CollisionModel();
        OrganizationListModel orgList = organizationService.findByNameSerialNumberCountryState(name, serialNumber, country, state, 0, 1);
        if(orgList != null && orgList.getCount() > 0) {
            collision.setCollision(true);
        }
        
        loggingService.insertLog( new LogModel(request.getRemoteHost(), "GET", "/org/collisionDetect/" + name + "/" + serialNumber + "/" + country + "/" + state, "", getCAName(), "OK", 0, collision.isCollision() ? 1 : 0, 0, 200) );
        return collision;        
    }
    
    @GetMapping(value="/validate/domain/{name}")
    @ApiOperation(value = "List of potential issues found for the supplied domain", response = String.class)
    public List<String> ValidateDomain(@ApiParam(name="name", value="Domain Name to valdiate", required = true) @PathVariable(value="name") String name) {
        List<String> results = new ArrayList<String>();
        try {
            results = validateDomain(URLDecoder.decode(name, StandardCharsets.UTF_16.toString()));
        } catch (UnsupportedEncodingException e) {
            results.add("Error decoding url string");
            e.printStackTrace();
        }
        if (results.size() < 1) {
            results.add("No issues detected");
        }
        
        return results;
    }

    @GetMapping(value="/validate/domain/topsites/{name}")
    @ApiOperation(value = "List of top sites that potentially match the supplied domain", response = String.class)
    public List<String> ValidateDomainTopSites(@ApiParam(name="name", value="Domain Name to valdiate", required = true) @PathVariable(value="name") String name) {
        List<String> results = new ArrayList<String>();
        try {
            results = homoglyphService.searchForTopSites(URLDecoder.decode(name, StandardCharsets.UTF_16.toString()));
        } catch (UnsupportedEncodingException e) {
            results.add("Error decoding url string");
            e.printStackTrace();
        }
        
        if (results.size() < 1) {
            results.add("No issues detected");
        }
        return results;
    }
    
    public boolean validateNewRecord(OrganizationInputModel organization) {
        boolean result = false;
        OrganizationListModel orgList = organizationService.findByOrganizationName(organization.getOrganizationName(), this.offset, this.limit);
        if(orgList == null || orgList.getCount() < 1) {
            orgList = organizationService.findByCommonName(organization.getCommonName(), 0, 1);
            if(orgList == null || orgList.getCount() < 1) {
                result = true;
            }
        }
        
        // Continue validating the record.
        if(result) {
            result = validateCountry(organization.getCountryName());
            if(result) {
                result = validateOrganizationName(organization.getOrganizationName());
            }
        }
        
        return result;        
    }
    
    // PRIVATE CALLS / HELPER FUNCTIONS
    
    private boolean validateCountry(String country) {
        boolean isValid = false;
        countryDisplayName = "";
        countryCode = "";
        // Try looking up by code
        try {
            Locale locale = new Locale("", country);
            if(locale != null && locale.getISO3Country() != null) {
                countryCode = locale.getCountry();
                countryDisplayName = locale.getDisplayCountry();
                isValid = true;
            }
        } catch (MissingResourceException e) {
            // Not a problem. This is fine. Try another way.
        }
        
        // Try looking up by name
        if(!isValid) {
            Map<String,Locale> map = new HashMap<String,Locale>();
            for (Locale locale : Locale.getAvailableLocales()) {
                map.put(locale.getDisplayCountry().toLowerCase(), locale);
            }
            
            Locale locale = map.get(country.toLowerCase());
            if(locale != null && locale.getISO3Country() != null) {
                isValid = true;
                countryDisplayName = locale.getDisplayCountry();
                countryCode = locale.getCountry();
            }
        }
        
        return isValid;
    }
    
    // We want to validate the organization entity type in some fashion. 
    // Some classes have been created in legal entity to this end, but it will depend on the country supplied
    private boolean validateOrganizationName(String orgName) {
        ILegalEntity le = LegalEntityFactory.GetLegalEntity(countryCode);
        return le.validate(orgName);
    }    
    
    // Return a list of errors/failed checks. Null if the string is fine.
    private List<String> validateDomain(String domain) {
        List<String> notes = new ArrayList<String>();
        if(StringUtils.isEmpty(domain)) {
            notes.add("Domain is empty");
        }
        else {
            if(containsMixedCharacterSet(domain.toLowerCase()) || homoglyphService.containsMixedAlphabets(domain)) {
                notes.add("Domain contains mixed character sets");
            }
            
            String convertedDomain = homoglyphService.convertHomoglyphs(domain);
            // TODO: Lookup converted domain in top 100 or whatever and see if there is a conflict?
            if(false == convertedDomain.toLowerCase().equals(domain.toLowerCase())) {
                notes.add("Domain converts to " + convertedDomain);
            }
            
            //homoglyphService.searchForTopSites(domain);
        }
        
        return notes;
    }

    private boolean containsMixedCharacterSet(String domain) {
        CheckResult result = new CheckResult();
        boolean failed = new SpoofChecker.Builder().build().failsChecks(IDN.toUnicode(domain.toLowerCase()), result);
        return failed && result.checks != SpoofChecker.WHOLE_SCRIPT_CONFUSABLE;
    }

}
