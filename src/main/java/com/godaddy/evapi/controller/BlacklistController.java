package com.godaddy.evapi.controller;

import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.godaddy.evapi.model.BlacklistDTOModel;
import com.godaddy.evapi.model.BlacklistInputModel;
import com.godaddy.evapi.model.BlacklistListModel;
import com.godaddy.evapi.model.BlacklistModel;
import com.godaddy.evapi.model.IdModel;
import com.godaddy.evapi.model.OrganizationListModel;
import com.godaddy.evapi.service.IBlacklistService;

import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@RestController
@RequestMapping(value = "/blacklist")
@Api(value = "Blacklist", description = "Resource for getting and modifying black list entries")
public class BlacklistController extends BaseController {
    @Autowired
    IBlacklistService blacklistService;
    
    // Get all records, paginated
    @GetMapping("")
    @ApiOperation(value = "Gets all blacklist records", response = BlacklistListModel.class)
    public ResponseEntity<Resource<BlacklistListModel>> getAll(HttpServletRequest request,
                @RequestParam( value="offset") Optional<Integer> offsetValue,
                @RequestParam( value="limit") Optional<Integer> limitValue) {
        setOffsetLimit(offsetValue,limitValue);
        BlacklistListModel entries = blacklistService.findAll(this.offset, this.limit);
        
        // Return found entries
        if(entries.getCount() > 0) {
            Resource<BlacklistListModel> resource = new Resource<>(entries, generateLinks(request, this.offset, this.limit, entries.getCount()));
            return ResponseEntity.ok(resource);
        }
        
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @DeleteMapping("/{id}")
    @ApiOperation(value = "Deletes a blacklist record by id, provided the authenticated caller inserted the record", response = HttpStatus.class)
    public ResponseEntity<HttpStatus> deleteBlacklist(@ApiParam(name="id", value="Record id", required = true) @PathVariable(value="id") String id) {
        boolean success = false;
        String ca = getCAName();
        if(id != null && id.trim().length() > 0) {
            BlacklistModel entry = blacklistService.findById(id);
            // If any entries, see if we can delete them
            // Validate the deleting ca is the same as the inserting one
            if(entry != null && entry.getInsertedBy().equals(ca)) {
                success = blacklistService.delete(id);
            }
        }
        
        if(success) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    
    @PutMapping("/{id}")
    @ApiOperation(value = "Updates a blacklist record by id, currently not implemented", response = HttpStatus.class)
    public ResponseEntity<HttpStatus> updateBlacklist(@ApiParam(name="id", value="Record id", required = true) @PathVariable(value = "id") String id) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
        
    @PostMapping("")
    @ApiOperation(value = "Create a new blacklist record", response = IdModel.class)
    public ResponseEntity<IdModel> createBlacklistEntry(@ApiParam(name="blacklistEntry", value="Blacklist entry to create") @RequestBody BlacklistInputModel blEntry) {
        String ca = getCAName();
        // Create our new id
        UUID id = UUID.randomUUID();
        
        // Insert the record is the data supplied is good
        if(validateNewRecord(blEntry)) {
            // Serial number is blank. Not sure we even need it.
            BlacklistModel blModel = new BlacklistModel(id, blEntry.getOrganizationName(), blEntry.getCommonName(), 
                        blEntry.getSerialNumber(), blEntry.getReason(), ca);
            if(blacklistService.save(blModel)) {
                // Return created record id
                return new ResponseEntity<IdModel>(new IdModel(id.toString()), HttpStatus.CREATED);
            }
        }
        
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    
    @GetMapping("/{id}")
    @ApiOperation(value = "Get a blacklist record by id", response = BlacklistModel.class)
    public ResponseEntity<BlacklistModel> getById(@ApiParam(name="id", value="Record id", required = true) @PathVariable(value="id") String id) {
        BlacklistModel result = blacklistService.findById(id);
        if(result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<BlacklistModel>(result, HttpStatus.OK);
    }
       
    @GetMapping("/commonName/{cname}")
    @ApiOperation(value = "Get a list of blacklist records matching a cName", response = BlacklistListModel.class)
    public ResponseEntity<Resource<BlacklistListModel>> getBlacklistByCName(HttpServletRequest request,
                @RequestParam( value="offset") Optional<Integer> offsetValue,
                @RequestParam( value="limit") Optional<Integer> limitValue,
                @ApiParam(name="cname", value="Common Name to search for", required = true) @PathVariable(value="cname") String cName) {
        setOffsetLimit(offsetValue,limitValue);
        if(cName != null && cName.length() > 2) {
            BlacklistListModel entries = blacklistService.findByCommonName(cName, this.offset, this.limit);
            if(entries != null && entries.getCount() > 0) {
                Resource<BlacklistListModel> resource = new Resource<>(entries, generateLinks(request, this.offset, this.limit, entries.getCount()));
                return ResponseEntity.ok(resource);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @GetMapping("/ca/{ca}")
    @ApiOperation(value = "Get a list of blacklist records inserted a CA", response = BlacklistListModel.class)
    public ResponseEntity<Resource<BlacklistListModel>> getBlacklistByCA(HttpServletRequest request,
                @RequestParam( value="offset") Optional<Integer> offsetValue,
                @RequestParam( value="limit") Optional<Integer> limitValue,
                @ApiParam(name="ca", value="The CA to search for", required = true) @PathVariable(value="ca") String ca) {
        setOffsetLimit(offsetValue,limitValue);
        if(ca != null && ca.length() > 1) {
            BlacklistListModel entries = blacklistService.findByCA(ca, this.offset, this.limit);
            if(entries != null && entries.getCount() > 0) {
                Resource<BlacklistListModel> resource = new Resource<>(entries, generateLinks(request, this.offset, this.limit, entries.getCount()));
                return ResponseEntity.ok(resource);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    // Private Helper functions
    
    // Just make sure they put something for each field and we are not duplicating an entry
    private boolean validateNewRecord(BlacklistInputModel blEntry) {
        boolean isValid = false;        
        if(blEntry != null) {
            // Check that everything is filled in 
            if(blEntry.getCommonName().trim().length() > 2 && blEntry.getOrganizationName().trim().length() > 0 &&
                    blEntry.getReason().trim().length() > 0) {
                // Make sure this record does not already exist
                BlacklistListModel entries = blacklistService.findByCommonName(blEntry.getCommonName().trim(), 0, 1);
                if(entries == null || entries.getCount() < 1) { 
                    isValid = true;
                }
            }
        }
    
        return isValid;
    }
}
