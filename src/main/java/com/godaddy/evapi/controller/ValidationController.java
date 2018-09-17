package com.godaddy.evapi.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.godaddy.evapi.model.ValidationInputModel;

@RestController
@RequestMapping(value = "/validation")
public class ValidationController {
    //@Autowired
    //IValidationService validationService;
    
    private int offset;
    private int limit;
    
    @GetMapping(value="")
    public void GetValidationList(@RequestParam( "offset" ) Optional<Integer> offset, @RequestParam( "limit" ) Optional<Integer> limit) {
        setOffsetLimit(offset, limit);
        // Connect to datastore
        
        // Get records
        
        // Build list.
        
        // Set count/offset/limit 
        
        // Return
    }
    
    @GetMapping("/{id}")
    public void GetValidationById(@PathVariable(value="id") String id) {
        // Connect to datastore
        
        // Get single record
        
        // Build ValidationItemModel
        
        // Return        
    }
    
    @GetMapping(value="/certificate/{certId}")
    public void GetValidationItems(@PathVariable(value="certId") String certId,
                @RequestParam( "offset" ) Optional<Integer> offset, @RequestParam( "limit" ) Optional<Integer> limit) {
        
    }
    
    // TODO: Handle file upload
    @PostMapping("")
    public void AddValidationItem(@RequestBody ValidationInputModel validationItem) {
        // Connect to data store
        
        // Create new object
        // Generate guid id
        // Set object data
        
        // Save record
        
        // return id
    }
    
    @PutMapping("/{id}")
    public void UpdateRecord() {
        // Connect to datastore
        
        // Get record
        
        // Update record
        
        // return
    }
    
    @DeleteMapping("/{id}")
    public void RemoveRecord() {
        // Connect to datastore
        
        // Get record
        
        // Set status to Removed
        
        // return
    }
    
    // PRIVATE CALLS / HELPER FUNCTIONS
    
    // Validate/sanity check the offset and limit values
    private void setOffsetLimit(Optional<Integer> offset, Optional<Integer> limit) {
        // Offset must not be negative
        this.offset =  offset.isPresent() && offset.get() > 0 ? offset.get() : 0;
        // Limit must be between 1 and 100. If 0, we would not return anything. Negative is right out.
        this.limit = limit.isPresent() && limit.get() < 101 && limit.get() > 0 ? limit.get() : 25;
    }
}
