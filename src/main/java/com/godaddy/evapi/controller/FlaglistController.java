package com.godaddy.evapi.controller;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
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

import com.godaddy.evapi.model.FlaglistInputModel;
import com.godaddy.evapi.model.FlaglistListModel;
import com.godaddy.evapi.model.FlaglistModel;
import com.godaddy.evapi.model.IdModel;
import com.godaddy.evapi.service.IFlaglistService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/flaglist")
@Api(value = "Flaglist", description = "Resource for getting and modifying flag list entries")
public class FlaglistController extends BaseController {
    @Autowired
    IFlaglistService flaglistService;
    
    // Get all records, paginated
    @GetMapping("")
    @ApiOperation(value = "Gets all flag list records", response = FlaglistListModel.class)
    public ResponseEntity<Resource<FlaglistListModel>> getAll(HttpServletRequest request,
                @RequestParam( value="offset") Optional<Integer> offsetValue,
                @RequestParam( value="limit") Optional<Integer> limitValue) {
        setOffsetLimit(offsetValue,limitValue);
        FlaglistListModel entries = flaglistService.findAll(this.offset, this.limit);
        
        // Return found entries
        if(entries.getCount() > 0) {
            Resource<FlaglistListModel> resource = new Resource<>(entries, generateLinks(request, this.offset, this.limit, entries.getCount()));
            return ResponseEntity.ok(resource);
        }
        
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @DeleteMapping("/{id}")
    @ApiOperation(value = "Deletes a flag list record by id, provided the authenticated caller inserted the record", response = HttpStatus.class)
    public ResponseEntity<HttpStatus> deleteFlaglist(@ApiParam(name="id", value="Record id", required = true) @PathVariable(value="id") String id) {
        boolean success = false;
        String ca = getCAName();
        if(id != null && id.trim().length() > 0) {
            FlaglistModel entry = flaglistService.findById(id);
            // If any entries, see if we can delete them
            // Validate the deleting ca is the same as the inserting one
            if(entry != null && entry.getInsertedBy().equals(ca)) {
                success = flaglistService.delete(id);
            }
        }
        
        if(success) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    
    @PutMapping("/{id}")
    @ApiOperation(value = "Updates a flag list record by id, currently not implemented", response = HttpStatus.class)
    public ResponseEntity<HttpStatus> updateFlaglist(@ApiParam(name="id", value="Record id", required = true) @PathVariable(value = "id") String id) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
        
    @PostMapping("")
    @ApiOperation(value = "Create a new flag list record", response = IdModel.class)
    public ResponseEntity<IdModel> createFlaglistEntry(@ApiParam(name="flaglistEntry", value="Flag list entry to create") @RequestBody FlaglistInputModel flEntry) {
        String ca = getCAName();
        // Create our new id
        UUID id = UUID.randomUUID();
        
        // Insert the record is the data supplied is good
        if(validateNewRecord(flEntry)) {
            // Serial number is blank. Not sure we even need it.
            FlaglistModel flModel = new FlaglistModel(id, flEntry.getOrganizationName(), flEntry.getCommonName(), 
                        flEntry.getSerialNumber(), flEntry.getReason(), ca, "ca", 2);
            if(flaglistService.save(flModel)) {
                // Return created record id
                return new ResponseEntity<IdModel>(new IdModel(id.toString()), HttpStatus.CREATED);
            }
        }

        if(validateModel(flEntry)) {
            // Update the date the record was last updated if it already exists.
            FlaglistListModel entries = flaglistService.findByCommonName(flEntry.getCommonName().trim(), 0, 1);
            if(entries != null && entries.getCount() > 0) { 
                for(FlaglistModel entry : entries.getFlaglistEntries()) {
                    entry.setLastUpdated(new Date());
                    flaglistService.save(entry);
                    return new ResponseEntity<IdModel>(new IdModel(id.toString()), HttpStatus.OK);
                }
            }
        }
        
        // Something is wrong.
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    
    @GetMapping("/{id}")
    @ApiOperation(value = "Get a flag list record by id", response = FlaglistModel.class)
    public ResponseEntity<FlaglistModel> getById(@ApiParam(name="id", value="Record id", required = true) @PathVariable(value="id") String id) {
        FlaglistModel result = flaglistService.findById(id);
        if(result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<FlaglistModel>(result, HttpStatus.OK);
    }
       
    @GetMapping("/commonName/{cname}")
    @ApiOperation(value = "Get a list of flag list records matching a cName", response = FlaglistListModel.class)
    public ResponseEntity<Resource<FlaglistListModel>> getFlaglistByCName(HttpServletRequest request,
                @RequestParam( value="offset") Optional<Integer> offsetValue,
                @RequestParam( value="limit") Optional<Integer> limitValue,
                @ApiParam(name="cname", value="Common Name to search for", required = true) @PathVariable(value="cname") String cName) {
        setOffsetLimit(offsetValue,limitValue);
        if(cName != null && cName.length() > 2) {
            FlaglistListModel entries = flaglistService.findByCommonName(cName, this.offset, this.limit);
            if(entries != null && entries.getCount() > 0) {
                Resource<FlaglistListModel> resource = new Resource<>(entries, generateLinks(request, this.offset, this.limit, entries.getCount()));
                return ResponseEntity.ok(resource);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @GetMapping("/ca/{ca}")
    @ApiOperation(value = "Get a list of flag list records inserted a CA", response = FlaglistListModel.class)
    public ResponseEntity<Resource<FlaglistListModel>> getFlaglistByCA(HttpServletRequest request,
                @RequestParam( value="offset") Optional<Integer> offsetValue,
                @RequestParam( value="limit") Optional<Integer> limitValue,
                @ApiParam(name="ca", value="The CA to search for", required = true) @PathVariable(value="ca") String ca) {
        setOffsetLimit(offsetValue,limitValue);
        if(ca != null && ca.length() > 1) {
            FlaglistListModel entries = flaglistService.findByCA(ca, this.offset, this.limit);
            if(entries != null && entries.getCount() > 0) {
                Resource<FlaglistListModel> resource = new Resource<>(entries, generateLinks(request, this.offset, this.limit, entries.getCount()));
                return ResponseEntity.ok(resource);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    // Private Helper functions
    
    // Just make sure they put something for each field and we are not duplicating an entry
    private boolean validateNewRecord(FlaglistInputModel flEntry) {
        boolean isValid = false;
        if(validateModel(flEntry) && !recordExists(flEntry)) { 
            isValid = true;
        }
    
        return isValid;
    }
    
    private boolean validateModel(FlaglistInputModel flEntry) {
        boolean isValid = false;
        if(flEntry != null) {
            // Check that everything is filled in 
            if(flEntry.getCommonName().trim().length() > 2 && flEntry.getOrganizationName().trim().length() > 0 &&
                    flEntry.getReason().trim().length() > 0) {
                isValid = true;
            }
        }
        
        return isValid;
    }

    private boolean recordExists(FlaglistInputModel flEntry) {
        boolean recordExists = false;
        FlaglistListModel entries = flaglistService.findByCommonName(flEntry.getCommonName().trim(), 0, 1);
        if(entries != null && entries.getCount() > 0) { 
            recordExists = true;
        }
        
        return recordExists;
    }
}
