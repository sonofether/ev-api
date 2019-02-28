package com.godaddy.evapi.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

@Service
public class AWSFileService implements IFileService {
    @Value("${aws.s3.bucket.name}")
    private String bucketName;
    
    @Override
    public boolean uploadFile(String filePath, String fileName) {
        final AmazonS3 s3 = AmazonS3Client.builder()
                    .withRegion(Regions.US_EAST_1)
                    .withCredentials(new DefaultAWSCredentialsProviderChain())
                    .build();
        try {
            PutObjectResult result = s3.putObject(bucketName, fileName, new File(filePath));
            // Make the object publicly readable
            s3.setObjectAcl(bucketName, fileName, CannedAccessControlList.PublicRead);
            return true;
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        return false;
    }

    @Override
    public boolean downloadFile(String fileName, String filePath) {
        final AmazonS3 s3 = AmazonS3Client.builder()
                    .withRegion(Regions.US_EAST_1)
                    .withCredentials(new DefaultAWSCredentialsProviderChain())
                    .build();
        try {
            S3Object o = s3.getObject(bucketName, fileName);
            S3ObjectInputStream s3is = o.getObjectContent();
            FileOutputStream fos = new FileOutputStream(new File(filePath));
            byte[] read_buf = new byte[1024];
            int read_len = 0;
            while ((read_len = s3is.read(read_buf)) > 0) {
                fos.write(read_buf, 0, read_len);
            }
            s3is.close();
            fos.close();
            return true;
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        
        return false;
    }

    @Override
    public boolean deleteFile(String fileName) {
        final AmazonS3 s3 = AmazonS3Client.builder()
                    .withRegion(Regions.US_EAST_1)
                    .withCredentials(new DefaultAWSCredentialsProviderChain())
                    .build();
        try {
            s3.deleteObject(bucketName, fileName);
            return true;
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
        }
        return false;
    }

    @Override
    public List<String> listFiles() {
        List<String> names = new ArrayList<String>();
        final AmazonS3 s3 = AmazonS3Client.builder()
                    .withRegion(Regions.US_EAST_1)
                    .withCredentials(new DefaultAWSCredentialsProviderChain())
                    .build();
        ListObjectsV2Result result = s3.listObjectsV2(bucketName);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        if(objects.size() > 0) {
            for (S3ObjectSummary os: objects) {
                names.add(os.getKey());
                //System.out.println("* " + os.getKey());
            }
        }
        
        return names;
    }

    @Override
    public boolean copyFile(String fileName, String newFileName) {
        final AmazonS3 s3 = AmazonS3Client.builder()
                    .withRegion(Regions.US_EAST_1)
                    .withCredentials(new DefaultAWSCredentialsProviderChain())
                    .build();
        try {
            CopyObjectResult result = s3.copyObject(bucketName, fileName, bucketName, newFileName);
            return true;
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
        }
        return false;
    }

    @Override
    public boolean renameFile(String fileName, String newFileName) {
        boolean result = false;
        if( copyFile(fileName, newFileName) ) {
           result = deleteFile(fileName);
        }
        return result;
    }

    @Override
    public boolean doesFileExist(String fileName) {
        final AmazonS3 s3 = AmazonS3Client.builder()
                    .withRegion(Regions.US_EAST_1)
                    .withCredentials(new DefaultAWSCredentialsProviderChain())
                    .build();
        try {
            return s3.doesObjectExist(bucketName, fileName);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
        }
        return false;
    }
    
    @Override
    public String getItemUrl(String fileName) {
        return "https://s3.amazonaws.com/" + bucketName + "/" + fileName;
    }
    
}
