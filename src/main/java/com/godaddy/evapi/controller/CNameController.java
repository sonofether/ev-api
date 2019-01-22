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

@RestController
@RequestMapping(value = "/cname")
public class CNameController {
    private int offset;
    private int limit;
    
    @Autowired
    IBlacklistService blacklistService;
    
    // Return not implemented for the basic CRUD operations.
    @GetMapping("")
    public ResponseEntity<HttpStatus> getAll() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> update() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
    
    @PostMapping("")
    public ResponseEntity<HttpStatus> createEntry() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BlacklistModel> getById(@PathVariable(value="id") String id) {
        BlacklistModel result = blacklistService.findById(id);
        if(result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<BlacklistModel>(result, HttpStatus.OK);
    }    
    
    @GetMapping("/flaglist/{cname}")
    public BlacklistDTOModel getBlacklistByCName(@PathVariable(value="cname") String cName) {
        BlacklistDTOModel result = new BlacklistDTOModel();
        if(cName != null && cName.length() > 2) {
            BlacklistListModel entries = blacklistService.findByCommonName(cName,0, 1);
            // If any entries, return true
            if(entries.getCount() > 0) {
                result.setBlacklisted(true);
            }
        }
                
        return result;
    }    
    
    // Private Helper functions

}