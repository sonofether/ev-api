package com.godaddy.evapi.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.config.EnableHypermediaSupport;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.godaddy.evapi.model.OrganizationListModel;
import com.godaddy.evapi.model.OrganizationModel;
import com.godaddy.evapi.model.ValidationInputModel;
import com.godaddy.evapi.model.ValidationItemModel;
import com.godaddy.evapi.model.ValidationListModel;
import com.godaddy.evapi.service.IFileService;
import com.godaddy.evapi.service.IOrganizationService;
import com.godaddy.evapi.service.IValidationService;

import io.jsonwebtoken.Claims;

@RestController
@RequestMapping(value = "/validation")
@EnableHypermediaSupport(type = { EnableHypermediaSupport.HypermediaType.HAL })
public class ValidationController {
    @Value("${files.storage.temp.path}")
    private String basePath;
    
    @Autowired
    IValidationService validationService;
    
    @Autowired
    IFileService fileService;
    
    @Autowired
    IOrganizationService organizationService;
    
    private int offset;
    private int limit;

    @GetMapping(value="")
    public ResponseEntity<Resource<ValidationListModel>> GetValidationList(HttpServletRequest request, 
                @RequestParam( "offset" ) Optional<Integer> offset, @RequestParam( "limit" ) Optional<Integer> limit) {
        setOffsetLimit(offset, limit);
        ValidationListModel viList = validationService.findAll(this.offset, this.limit);
        if(viList.getCount() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }        
            
        Resource<ValidationListModel> resource = new Resource<>(viList, generateLinks(request, this.offset, this.limit, viList.getCount()));
        return ResponseEntity.ok(resource);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ValidationItemModel> GetValidationById(@PathVariable(value="id") String id) {
        ValidationItemModel org = validationService.findById(id);        
        if(org == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<ValidationItemModel>(org, HttpStatus.OK);      
    }
    
    @GetMapping(value="/certificate/{certificateId}")
    public ResponseEntity<Resource<ValidationListModel>> GetValidationItems(HttpServletRequest request,
                @PathVariable(value="certificateId") String certificateId,
                @RequestParam( "offset" ) Optional<Integer> offset, @RequestParam( "limit" ) Optional<Integer> limit) {
        setOffsetLimit(offset, limit);
        ValidationListModel viList = validationService.findByCertificateId(certificateId, this.offset, this.limit);
        if(viList == null || viList.getCount() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }        
            
        Resource<ValidationListModel> resource = new Resource<>(viList, generateLinks(request, this.offset, this.limit, viList.getCount()));
        return ResponseEntity.ok(resource);
    }
    
    // Generate a record and pass back where they can upload the file
    @PostMapping("")
    public ResponseEntity<String> AddValidationItem(@RequestBody ValidationInputModel validationItem) {       
        // Make sure this CA owns the cert they are attempting to work with.
        // Grab the auth token, convert to json, and get the ca value
        Claims token = (Claims)SecurityContextHolder.getContext().getAuthentication().getCredentials();
        String ca = (String)token.get("ca").toString();
        OrganizationModel org = organizationService.findById(validationItem.getCertId().toString());
        if(org == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        else if(!org.getCa().equals(ca)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        
        // Create our new id
        UUID id = UUID.randomUUID();
        ValidationItemModel vi = new ValidationItemModel(id, "", "", validationItem.getCertId(), validationItem.getValidates(), 1);

        if(validationService.save(vi)) {
            // TODO: Write data to the block chain           
            return new ResponseEntity<String>( "{\"id\": \"" + id.toString() + "\"}", HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } // AddValidationItem
    
    @PutMapping("/{id}")
    public ResponseEntity<String> UpdateRecord(@PathVariable(value="id") String id,
                @RequestParam("file") MultipartFile file) {
        // Make sure this is a valid record
        ValidationItemModel vi = validationService.findById(id);
        if( vi == null ) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Make sure this CA owns the cert they are attempting to work with.
        // Grab the auth token, convert to json, and get the ca value
        Claims token = (Claims)SecurityContextHolder.getContext().getAuthentication().getCredentials();
        String ca = (String)token.get("ca").toString();        
        OrganizationModel org = organizationService.findById(vi.getCertificateId().toString());
        if(org == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        else if(!org.getCa().equals(ca)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // TODO: AWS S3: Url will be the AWS location + the id
        String itemUrl = "";
        boolean success = false;
        
        // Unique identifier for file to upload - (just use the id)
        try {
            writeFile(file, basePath + id);            
            // TODO: AWS S3:  Store file to AWS        
            //if(fileService.uploadFile(basePath + id, id))
            if(true)
            {
                // TODO: AWS S3: Generate URL where file can be accessed
                // TODO: AWS S3: No need to update the record with the url, just delete/overwrite the old one.

                // Update the record.
                vi.setFileName(file.getOriginalFilename());
                validationService.save(vi);
                success = true;
            }
        } catch (Exception ex) {
            
        }
        finally {
            // TODO: AWS S3: Uncomment this once we get s3 working
            //deleteFile(basePath + id);
        }
        
        if(success) {
            return new ResponseEntity<String>("{\"id\": \"" + id.toString()+ "\"}", HttpStatus.OK);
        }
        
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    } // UpdateRecord
    
    // We do not support deletes
    @DeleteMapping("/{id}")
    public ResponseEntity<String> RemoveRecord(@PathVariable(value="id") String id) {
        ValidationItemModel vi = validationService.findById(id);
        if( vi == null ) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        vi.setStatus(0);
        validationService.save(vi);
        return new ResponseEntity<>(HttpStatus.OK);
    } // RemoveRecord
    
    
    // PRIVATE CALLS / HELPER FUNCTIONS
    
    private File writeFile(MultipartFile file, String fileName) throws Exception {
        File newFile = new File(fileName);
        newFile.createNewFile(); 
        FileOutputStream fos = new FileOutputStream(newFile); 
        fos.write(file.getBytes());
        fos.close();
        return newFile;
    }
    
    // Validate/sanity check the offset and limit values
    private void setOffsetLimit(Optional<Integer> offset, Optional<Integer> limit) {
        // Offset must not be negative
        this.offset =  offset.isPresent() && offset.get() > 0 ? offset.get() : 0;
        // Limit must be between 1 and 100. If 0, we would not return anything. Negative is right out.
        this.limit = limit.isPresent() && limit.get() < 101 && limit.get() > 0 ? limit.get() : 25;
    }
    
    private List<Link> generateLinks(HttpServletRequest request, int offset, int limit, int size) {
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
    
    private void deleteFile(String filePath) {
        try {
            Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException ex) {
            // Well, that didn't work. Not much we can do.
        }
        return;
    }
}
