package com.godaddy.evapi.controller;

import java.util.Calendar;
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
import com.godaddy.evapi.model.LogModel;
import com.godaddy.evapi.service.IFlaglistService;
import com.godaddy.evapi.service.ILoggingService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/flaglist")
@Api(value = "Flaglist", description = "Resource for getting and modifying flag list entries")
public class FlaglistController extends BaseController {
    @Autowired
    IFlaglistService flaglistService;
    
    @Autowired
    ILoggingService loggingService;
    
    @Autowired
    HttpServletRequest request;
    
    // Get all records, paginated
    @GetMapping("")
    @ApiOperation(value = "Gets all flag list records", response = FlaglistListModel.class)
    public ResponseEntity<Resource<FlaglistListModel>> getAll(
                @RequestParam( value="offset") Optional<Integer> offsetValue,
                @RequestParam( value="limit") Optional<Integer> limitValue,
                @RequestParam( value="filters", defaultValue="") String filters) {
        setOffsetLimit(offsetValue,limitValue);
        FlaglistListModel entries = flaglistService.findByVariableArguments(filters, this.offset, this.limit);
        // Return found entries
        if(entries != null && entries.getCount() > 0) {
            Resource<FlaglistListModel> resource = new Resource<>(entries, generateLinks(request, this.offset, this.limit, entries.getCount()));
            loggingService.insertLog( new LogModel(request.getRemoteHost(), "GET", "/flaglist", filters, getCAName(), "OK", this.offset, entries.getCount(), this.limit, 200) );
            return ResponseEntity.ok(resource);
        }

        loggingService.insertLog( new LogModel(request.getRemoteHost(), "GET", "/flaglist", filters, getCAName(), "NOT_FOUND", this.offset, 0, this.limit, 404) );
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
            loggingService.insertLog( new LogModel(request.getRemoteHost(), "DELETE", "/flaglist/" + id, "", getCAName(), "OK", 0, 1, 0, 200) );
            return new ResponseEntity<>(HttpStatus.OK);
        }
        
        loggingService.insertLog( new LogModel(request.getRemoteHost(), "DELETE", "/flaglist/" + id, "", getCAName(), "BAD_REQUEST", 0, 1, 0, 400) );
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    
    @PutMapping("/{id}")
    @ApiOperation(value = "Updates a flag list record by id, currently not implemented", response = HttpStatus.class)
    public ResponseEntity<HttpStatus> updateFlaglist(@ApiParam(name="id", value="Record id", required = true) @PathVariable(value = "id") String id) {
        loggingService.insertLog( new LogModel(request.getRemoteHost(), "PUT", "/flaglist/" + id, "", getCAName(), "NOT_IMPLEMENTED", 0, 1, 0, 501) );
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
                        flEntry.getSerialNumber(), flEntry.getReason(), ca, "ca", 1);
            if(flaglistService.save(flModel)) {
                // Return created record id
                loggingService.insertLog( new LogModel(request.getRemoteHost(), "POST", "/flaglist/", id.toString(), getCAName(), "CREATED", 0, 1, 0, 201) );
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
                    loggingService.insertLog( new LogModel(request.getRemoteHost(), "POST", "/flaglist/", id.toString(), getCAName(), "OK", 0, 1, 0, 200) );
                    return new ResponseEntity<IdModel>(new IdModel(id.toString()), HttpStatus.OK);
                }
            }
        }
        
        // Something is wrong.
        loggingService.insertLog( new LogModel(request.getRemoteHost(), "POST", "/flaglist", flEntry.getOrganizationName() + " " + flEntry.getCommonName(), getCAName(),
                    "BAD_REQUEST", 0, 1, 0, 400) );
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    
    @GetMapping("/{id}")
    @ApiOperation(value = "Get a flag list record by id", response = FlaglistModel.class)
    public ResponseEntity<FlaglistModel> getById(@ApiParam(name="id", value="Record id", required = true) @PathVariable(value="id") String id) {
        FlaglistModel result = flaglistService.findById(id);
        
        String ca = getCAName();
        String host = request.getRemoteHost();
        
        if(result == null) {
            loggingService.insertLog( new LogModel(request.getRemoteHost(), "GET", "/flaglist/" + id, "", getCAName(), "NOT_FOUND", 0, 0, 0, 404) );
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        loggingService.insertLog( new LogModel(request.getRemoteHost(), "GET", "/flaglist/" + id, "", getCAName(), "OK", 0, 1, 0, 404) );
        return new ResponseEntity<FlaglistModel>(result, HttpStatus.OK);
    }
    
    @GetMapping("/ofac")
    @ApiOperation(value = "Get a list of flag list records matching ofac source", response = FlaglistListModel.class)
    public ResponseEntity<Resource<FlaglistListModel>> getFlaglistByOfacSource(@RequestParam( value="offset") Optional<Integer> offsetValue,
                @RequestParam( value="limit") Optional<Integer> limitValue) {
        setOffsetLimit(offsetValue,limitValue);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        FlaglistListModel entries = flaglistService.findByDateAndSource( cal.getTime(), "OFAC", this.offset, this.limit);
        if(entries != null && entries.getCount() > 0) {
            Resource<FlaglistListModel> resource = new Resource<>(entries, generateLinks(request, this.offset, this.limit, entries.getCount()));
            loggingService.insertLog( new LogModel(request.getRemoteHost(), "GET", "/flaglist/ofac/", "", getCAName(), "OK", this.offset, entries.getCount(), this.limit, 200) );
            return ResponseEntity.ok(resource);
        }
        
        loggingService.insertLog( new LogModel(request.getRemoteHost(), "GET", "/flaglist/ofac/", "", getCAName(), "OK", this.offset, 0, this.limit, 404) );
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
       
    @GetMapping("/commonName/{cname}")
    @ApiOperation(value = "Get a list of flag list records matching a cName", response = FlaglistListModel.class)
    public ResponseEntity<Resource<FlaglistListModel>> getFlaglistByCName(
                @RequestParam( value="offset") Optional<Integer> offsetValue,
                @RequestParam( value="limit") Optional<Integer> limitValue,
                @ApiParam(name="cname", value="Common Name to search for", required = true) @PathVariable(value="cname") String cName) {
        setOffsetLimit(offsetValue,limitValue);
        if(cName != null && cName.length() > 2) {
            FlaglistListModel entries = flaglistService.findByCommonName(cName, this.offset, this.limit);
            if(entries != null && entries.getCount() > 0) {
                Resource<FlaglistListModel> resource = new Resource<>(entries, generateLinks(request, this.offset, this.limit, entries.getCount()));
                loggingService.insertLog( new LogModel(request.getRemoteHost(), "GET", "/flaglist/commonName/" + cName, "", getCAName(), "OK", this.offset, entries.getCount(), this.limit, 200) );
                return ResponseEntity.ok(resource);
            }
        }
        
        loggingService.insertLog( new LogModel(request.getRemoteHost(), "GET", "/flaglist/commonName/" + cName, "", getCAName(), "OK", this.offset, 0, this.limit, 404) );
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    
    }

    @GetMapping("/name/{name}")
    @ApiOperation(value = "Get a list of flag list records matching an organization name", response = FlaglistListModel.class)
    public ResponseEntity<Resource<FlaglistListModel>> getFlaglistByName(
                @RequestParam( value="offset") Optional<Integer> offsetValue,
                @RequestParam( value="limit") Optional<Integer> limitValue,
                @ApiParam(name="cname", value="Common Name to search for", required = true) @PathVariable(value="name") String name) {
        setOffsetLimit(offsetValue,limitValue);
        if(name != null && name.length() > 2) {
            FlaglistListModel entries = flaglistService.findByOrganizationName(name, this.offset, this.limit);
            if(entries != null && entries.getCount() > 0) {
                Resource<FlaglistListModel> resource = new Resource<>(entries, generateLinks(request, this.offset, this.limit, entries.getCount()));
                loggingService.insertLog( new LogModel(request.getRemoteHost(), "GET", "/flaglist/name/" + name, "", getCAName(), "OK", this.offset, entries.getCount(), this.limit, 200) );
                return ResponseEntity.ok(resource);
            }
        }
        
        loggingService.insertLog( new LogModel(request.getRemoteHost(), "GET", "/flaglist/name/" + name, "", getCAName(), "OK", this.offset, 0, this.limit, 404) );
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @GetMapping("/ca/{ca}")
    @ApiOperation(value = "Get a list of flag list records inserted a CA", response = FlaglistListModel.class)
    public ResponseEntity<Resource<FlaglistListModel>> getFlaglistByCA(
                @RequestParam( value="offset") Optional<Integer> offsetValue,
                @RequestParam( value="limit") Optional<Integer> limitValue,
                @ApiParam(name="ca", value="The CA to search for", required = true) @PathVariable(value="ca") String ca) {
        setOffsetLimit(offsetValue,limitValue);
        if(ca != null && ca.length() > 1) {
            FlaglistListModel entries = flaglistService.findByCA(ca, this.offset, this.limit);
            if(entries != null && entries.getCount() > 0) {
                Resource<FlaglistListModel> resource = new Resource<>(entries, generateLinks(request, this.offset, this.limit, entries.getCount()));
                loggingService.insertLog( new LogModel(request.getRemoteHost(), "GET", "/flaglist/ca/" + ca, "", getCAName(), "OK", this.offset, entries.getCount(), this.limit, 200) );
                return ResponseEntity.ok(resource);
            }
        }
        
        loggingService.insertLog( new LogModel(request.getRemoteHost(), "GET", "/flaglist/ca/" + ca, "", getCAName(), "OK", this.offset, 0, this.limit, 404) );
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/source/{source}")
    @ApiOperation(value = "Get a list of flag list records matching a source", response = FlaglistListModel.class)
    public ResponseEntity<Resource<FlaglistListModel>> getFlaglistBySource(
                @RequestParam( value="offset") Optional<Integer> offsetValue,
                @RequestParam( value="limit") Optional<Integer> limitValue,
                @ApiParam(name="source", value="Source to search for", required = true) @PathVariable(value="source") String source) {
        setOffsetLimit(offsetValue,limitValue);
        if(source != null && source.length() > 1) {
            FlaglistListModel entries = flaglistService.findBySource(source, this.offset, this.limit);
            if(entries != null && entries.getCount() > 0) {
                Resource<FlaglistListModel> resource = new Resource<>(entries, generateLinks(request, this.offset, this.limit, entries.getCount()));
                loggingService.insertLog( new LogModel(request.getRemoteHost(), "GET", "/flaglist/source/" + source, "", getCAName(), "OK", this.offset, entries.getCount(), this.limit, 200) );
                return ResponseEntity.ok(resource);
            }
        }
        
        loggingService.insertLog( new LogModel(request.getRemoteHost(), "GET", "/flaglist/source/" + source, "", getCAName(), "OK", this.offset, 0, this.limit, 404) );
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
