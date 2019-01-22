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
import org.springframework.web.bind.annotation.RequestMapping;
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

@RestController
@RequestMapping(value = "/blacklist")
public class BlacklistController extends BaseController {
    private int offset;
    private int limit;
    
    @Autowired
    IBlacklistService blacklistService;
    
    // Get all records, paginated
    @GetMapping("")
    public ResponseEntity<Resource<BlacklistListModel>> getAll(HttpServletRequest request,
                @RequestParam( value="offset") Optional<Integer> offset,
                @RequestParam( value="limit") Optional<Integer> limit) {
        setOffsetLimit(offset,limit);
        BlacklistListModel entries = blacklistService.findAll(this.offset, this.limit);
        
        // Return found entries
        if(entries.getCount() > 0) {
            Resource<BlacklistListModel> resource = new Resource<>(entries, generateLinks(request, this.offset, this.limit, entries.getCount()));
            return ResponseEntity.ok(resource);
        }
        
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @DeleteMapping(value="/{id}")
    public ResponseEntity<HttpStatus> deleteBlacklist(@PathVariable(value="id") String id) {
        boolean success = false;
        String ca = getCAName();
        if(id != null && id.trim().length() > 0) {
            BlacklistModel entry = blacklistService.findById(id);
            // If any entries, see if we can delete them
            if(entry != null) {
                success = blacklistService.delete(id);
            }
        }
        
        if(success) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    
    @PutMapping(value="/{id}")
    public ResponseEntity<HttpStatus> updateBlacklist(@PathVariable(value = "id") String id) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
        
    @PostMapping("")
    public ResponseEntity<IdModel> createBlacklistEntry(BlacklistInputModel blEntry) {
        String ca = getCAName();
        // Create our new id
        UUID id = UUID.randomUUID();
        
        // Insert the record is the data supplied is good
        if(validateNewRecord(blEntry)) {
            // Serial number is blank. Not sure we even need it.
            BlacklistModel blModel = new BlacklistModel(id, blEntry.getOrganizationName(), blEntry.getCommonName(), 
                        "", blEntry.getReason(), ca);
            if(blacklistService.save(blModel)) {
                // Return created record id
                return new ResponseEntity<IdModel>(new IdModel(id.toString()), HttpStatus.OK);
            }
        }
        
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BlacklistModel> getById(@PathVariable(value="id") String id) {
        BlacklistModel result = blacklistService.findById(id);
        if(result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<BlacklistModel>(result, HttpStatus.OK);
    }
       
    @GetMapping("/commonName/{cname}")
    public ResponseEntity<Resource<BlacklistListModel>> getBlacklistByCName(HttpServletRequest request,
                @RequestParam( value="offset") Optional<Integer> offset,
                @RequestParam( value="limit") Optional<Integer> limit,
                @PathVariable(value="cname") String cName) {
        setOffsetLimit(offset,limit);
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
    public ResponseEntity<Resource<BlacklistListModel>> getBlacklistByCA(HttpServletRequest request,
                @RequestParam( value="offset") Optional<Integer> offset,
                @RequestParam( value="limit") Optional<Integer> limit,
                @PathVariable(value="ca") String ca) {
        setOffsetLimit(offset,limit);
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
