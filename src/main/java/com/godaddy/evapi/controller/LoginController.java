package com.godaddy.evapi.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/login")
public class LoginController {    
    // This will never actually get hit. The request is handled by the authentication filter. We can just do some testing in here for now.
    @PostMapping("")
    @ApiOperation(value = "Authenticate the user using Basic Auth", response = HttpStatus.class)
    public ResponseEntity<HttpStatus>  authenticateUser(@RequestHeader("authorization") String authString, HttpServletResponse response) {       
        return new ResponseEntity<>(HttpStatus.OK);
    }
}