package com.godaddy.evapi.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.hateoas.Link;
import org.springframework.security.core.context.SecurityContextHolder;

import io.jsonwebtoken.Claims;

public class BaseController {
    protected int offset;
    protected int limit;
    
    public BaseController() {
        this.offset = 0;
        this.limit = 0;
    }
    
    // Validate/sanity check the offset and limit values
    protected void setOffsetLimit(Optional<Integer> offset, Optional<Integer> limit) {
        // Offset must not be negative
        this.offset =  offset.isPresent() && offset.get() > 0 ? offset.get() : 0;
        // Limit must be between 1 and 100. If 0, we would not return anything. Negative is right out.
        this.limit = limit.isPresent() && limit.get() < 101 && limit.get() > 0 ? limit.get() : 25;
    }
    
    protected String getCAName() {
        // Grab the auth token, convert to json, and get the ca value
        Claims token = (Claims)SecurityContextHolder.getContext().getAuthentication().getCredentials();
        String ca = (String)token.get("ca").toString();
        return ca;
    }
    
    protected List<Link> generateLinks(HttpServletRequest request, int offset, int limit, int size) {
        List<Link> links = new ArrayList<Link>();
        String self = request.getRequestURL().toString() + (request.getQueryString() != null && request.getQueryString().length() > 0 ? "?" + request.getQueryString() : "" );
        links.add(new Link(self).withRel("self"));
        links.add(new Link(request.getRequestURL().toString() + "?offset=0&limit=" + limit).withRel("first"));
        
        if(offset != 0) {
            int prevOffset = (offset - limit) > 0 ? offset - limit : 0;
            Link link = new Link(request.getRequestURL().toString() + "?offset=" + prevOffset + "&limit=" + limit).withRel("prev");
            links.add(link);
        }
        
        if(size >= limit) {
            int nextOffset = offset + size;
            Link link = new Link(request.getRequestURL().toString() + "?offset=" + nextOffset + "&limit=" + limit).withRel("next");
            links.add(link);
        }
        
        return links;
    }
}
