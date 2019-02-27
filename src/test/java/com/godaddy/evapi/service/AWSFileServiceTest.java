package com.godaddy.evapi.service;

import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.util.UrlPathHelper;

public class AWSFileServiceTest {
    private String bucketName = "aws.bucket";
    
    @InjectMocks
    private AWSFileService fileService;
    
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        ReflectionTestUtils.setField(fileService, "bucketName", bucketName, String.class);
    }
    
    @Test
    public void testAwsUpload() {
        boolean result = false;
        String filePath = "/Users/asink/Downloads/doc_upload.jpg";
        UUID uuid = UUID.randomUUID();
        String fileName = uuid.toString();
        /*
        List<String> fileNames = fileService.listFiles();
        
        assert(fileNames.size() > 0);
        for (String temp : fileNames) {
            System.out.println(temp);
            // Works!
            //result = fileService.downloadFile(temp, "/tmp/tempfile.jpg");
            //assert(result);
            //break;
        }
        // Works!
        //result = fileService.uploadFile(filePath, fileName);
        //assert(result);
        //result = fileService.renameFile(fileName, UUID.randomUUID().toString());
        //assert(result);
         /* 
         */
    }
}
