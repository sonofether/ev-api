package com.godaddy.evapi.service;

import java.util.List;

public interface IFileService {
    // File name needs to be unique
    public boolean uploadFile(String filePath, String fileName);
    
    // This is the unique name we uploaded the file as, and where we want the file written
    public boolean downloadFile(String fileName, String filePath);

    public boolean deleteFile(String fileName);
    
    public List<String> listFiles();
    
    public boolean doesFileExist(String fileName);
    
    public boolean copyFile(String fileName, String newFileName);
    
    // This is just a copy followed by a delete.
    public boolean renameFile(String fileName, String newFileName);    
    
}