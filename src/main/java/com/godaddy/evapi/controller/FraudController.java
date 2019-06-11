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

import com.godaddy.evapi.model.FraudInputModel;
import com.godaddy.evapi.model.FraudListModel;
import com.godaddy.evapi.model.FraudModel;
import com.godaddy.evapi.model.IdModel;
import com.godaddy.evapi.model.LogModel;
import com.godaddy.evapi.service.IFraudService;
import com.godaddy.evapi.service.ILoggingService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/fraud")
@Api(value = "Fraud", description = "Resource for getting and modifying fraud entries")
public class FraudController extends BaseController {

    @Autowired
    IFraudService fraudService;
    
    @Autowired
    ILoggingService loggingService;
    
    @Autowired
    HttpServletRequest request;
    
    // Get all records, paginated
    @GetMapping("")
    @ApiOperation(value = "Gets all fraud records", response = FraudListModel.class)
    public ResponseEntity<Resource<FraudListModel>> getAll(
                @RequestParam( value="offset") Optional<Integer> offsetValue,
                @RequestParam( value="limit") Optional<Integer> limitValue,
                @RequestParam( value="filters", defaultValue="") String filters) {
        setOffsetLimit(offsetValue,limitValue);
        FraudListModel entries = fraudService.findByVariableArguments(filters, this.offset, this.limit);
        // Return found entries
        if(entries != null && entries.getCount() > 0) {
            Resource<FraudListModel> resource = new Resource<>(entries, generateLinks(request, this.offset, this.limit, entries.getCount()));
            loggingService.insertLog( new LogModel(request.getRemoteHost(), "GET", "/fraud", filters, getCAName(), "OK", this.offset, entries.getCount(), this.limit, 200) );
            return ResponseEntity.ok(resource);
        }
        
        loggingService.insertLog( new LogModel(request.getRemoteHost(), "GET", "/fraud", filters, getCAName(), "NOT_FOUND", this.offset, 0, this.limit, 404) );
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @DeleteMapping("/{id}")
    @ApiOperation(value = "Deletes a fraud record by id, provided the authenticated caller inserted the record", response = HttpStatus.class)
    public ResponseEntity<HttpStatus> deleteFraud(@ApiParam(name="id", value="Record id", required = true) @PathVariable(value="id") String id) {
        boolean success = false;
        String ca = getCAName();
        if(id != null && id.trim().length() > 0) {
            FraudModel entry = fraudService.findById(id);
            // If any entries, see if we can delete them
            // Validate the deleting ca is the same as the inserting one
            if(entry != null && entry.getInsertedBy().equals(ca)) {
                success = fraudService.delete(id);
            }
        }
        
        if(success) {
            loggingService.insertLog( new LogModel(request.getRemoteHost(), "DELETE", "/fraud/" + id, "", getCAName(), "OK", 0, 1, 0, 200) );
            return new ResponseEntity<>(HttpStatus.OK);
        }
        
        loggingService.insertLog( new LogModel(request.getRemoteHost(), "DELETE", "/fraud/" + id, "", getCAName(), "BAD_REQUEST", 0, 1, 0, 400) );
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    
    @PutMapping("/{id}")
    @ApiOperation(value = "Updates a fraud record by id, currently not implemented", response = HttpStatus.class)
    public ResponseEntity<HttpStatus> updateFraud(@ApiParam(name="id", value="Record id", required = true) @PathVariable(value = "id") String id) {
        loggingService.insertLog( new LogModel(request.getRemoteHost(), "PUT", "/fraud/" + id, "", getCAName(), "NOT_IMPLEMENTED", 0, 1, 0, 501) );
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
        
    @PostMapping("")
    @ApiOperation(value = "Create a new fraud record", response = IdModel.class)
    public ResponseEntity<IdModel> createFraudEntry(@ApiParam(name="fraudEntry", value="Fraud entry to create") @RequestBody FraudInputModel fraudEntry) {
        String ca = getCAName();
        // Create our new id
        UUID id = UUID.randomUUID();
        
        if(validateModel(fraudEntry)) {
            FraudListModel entries = fraudService.findByKeyword(fraudEntry.getKeyword().trim(), 0, 1);
            if(entries != null && entries.getCount() > 0) { 
                // Update the date the record was last updated if it already exists.
                for(FraudModel entry : entries.getFraudList()) {
                    entry.setLastUpdated(new Date());
                    fraudService.save(entry);
                    loggingService.insertLog( new LogModel(request.getRemoteHost(), "POST", "/fraud/", id.toString(), getCAName(), "OK", 0, 1, 0, 200) );
                    return new ResponseEntity<IdModel>(new IdModel(id.toString()), HttpStatus.OK);
                }
            } else {
                FraudModel fraud = new FraudModel(id, fraudEntry.getKeyword(), new Date(), fraudEntry.getType(), fraudEntry.getDescription(), ca);
                if(fraudService.save(fraud)) {
                    // Return created record id
                    loggingService.insertLog( new LogModel(request.getRemoteHost(), "POST", "/fraud/", id.toString(), getCAName(), "CREATED", 0, 1, 0, 201) );
                    return new ResponseEntity<IdModel>(new IdModel(id.toString()), HttpStatus.CREATED);
                }                
            }
        }
        
        // Something is wrong.
        loggingService.insertLog( new LogModel(request.getRemoteHost(), "POST", "/fraud", fraudEntry.getKeyword() + " " + fraudEntry.getType() + "" + fraudEntry.getDescription(),
                    getCAName(), "BAD_REQUEST", 0, 1, 0, 400) );
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    
    @GetMapping("/{id}")
    @ApiOperation(value = "Get a fraud record by id", response = FraudModel.class)
    public ResponseEntity<FraudModel> getById(@ApiParam(name="id", value="Record id", required = true) @PathVariable(value="id") String id) {
        FraudModel result = fraudService.findById(id);
        
        String ca = getCAName();
        String host = request.getRemoteHost();
        
        if(result == null) {
            loggingService.insertLog( new LogModel(request.getRemoteHost(), "GET", "/fraud/" + id, "", getCAName(), "NOT_FOUND", 0, 0, 0, 404) );
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        loggingService.insertLog( new LogModel(request.getRemoteHost(), "GET", "/fraud/" + id, "", getCAName(), "OK", 0, 1, 0, 404) );
        return new ResponseEntity<FraudModel>(result, HttpStatus.OK);
    }
    
    // Private functions
    
    private boolean validateModel(FraudInputModel fraudRecord) {
        boolean isValid = true;
        if(fraudRecord.getKeyword().length() < 1 || fraudRecord.getType() < 0) {
            isValid = false;
        }
        
        return isValid;
    }
            
}
