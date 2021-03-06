package com.godaddy.evapi.controller;

import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.godaddy.evapi.service.IBlacklistService;

import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/cname")
@Api(value = "CName", description = "Resource for determining if an entry is in the flag list")
public class CNameController {
    @Autowired
    IBlacklistService blacklistService;
    
    // Return not implemented for the basic CRUD operations.
    @GetMapping("")
    @ApiOperation(value = "Get all flag list records. Currently not implemented - Use the flaglist enpoint instead", response = HttpStatus.class)
    public ResponseEntity<HttpStatus> getAll() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
    
    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete flag list records. Currently not implemented - Use the flaglist enpoint instead", response = HttpStatus.class)
    public ResponseEntity<HttpStatus> delete(@ApiParam(name="id", value="Record id", required = true) @PathVariable(value="id") String id) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
    
    @PutMapping("/{id}")
    @ApiOperation(value = "Update flag list records. Currently not implemented - Use the flaglist enpoint instead", response = HttpStatus.class)
    public ResponseEntity<HttpStatus> update(@ApiParam(name="id", value="Record id", required = true) @PathVariable(value="id") String id) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
    
    @PostMapping("")
    @ApiOperation(value = "Create all flag list records. Currently not implemented - Use the flaglist enpoint instead", response = HttpStatus.class)
    public ResponseEntity<HttpStatus> createEntry() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
    
    @GetMapping("/{id}")
    @ApiOperation(value = "Get a flag list record by id.", response = BlacklistModel.class)
    public ResponseEntity<BlacklistModel> getById(@ApiParam(name="id", value="Record id", required = true) @PathVariable(value="id") String id) {
        BlacklistModel result = blacklistService.findById(id);
        if(result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<BlacklistModel>(result, HttpStatus.OK);
    }    
    
    @GetMapping("/flaglist/{cname}")
    @ApiOperation(value = "True/False lookup for a matching flag list record by cName.", response = BlacklistDTOModel.class)
    public BlacklistDTOModel getBlacklistByCName(@ApiParam(name="cname", value="cName to search for", required = true) @PathVariable(value="cname") String cName) {
        BlacklistDTOModel result = new BlacklistDTOModel();
        if(cName != null && cName.length() > 2) {
            BlacklistListModel entries = blacklistService.findByCommonName(cName,0, 1);
            // If any entries, return true
            if(entries != null && entries.getCount() > 0) {
                result.setBlacklisted(true);
            }
        }
                
        return result;
    }    
    
    // Private Helper functions
}
