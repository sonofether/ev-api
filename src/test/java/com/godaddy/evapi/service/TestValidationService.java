package com.godaddy.evapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.godaddy.evapi.model.ValidationItemModel;
import com.godaddy.evapi.model.ValidationListModel;

public class TestValidationService implements IValidationService {    
    public static ValidationListModel generateValidationList() {
        ValidationListModel validationList = new ValidationListModel();
        validationList.setCount(1);
        validationList.setOffset(0);
        validationList.setLimit(25);
        List<ValidationItemModel> validationItems = new ArrayList<ValidationItemModel>();
        validationItems.add(generateValidationItem());
        validationList.setValidationItems(validationItems);
        return validationList;
    }
    
    public static ValidationItemModel generateValidationItem() {
        ValidationItemModel validationItem = new ValidationItemModel();
        validationItem.setCertificateId(UUID.randomUUID());
        validationItem.setFileName("some_file.txt");
        validationItem.setId(UUID.randomUUID());
        validationItem.setItemUrl("https://example.com/service/some_file.txt");
        validationItem.setValidates("owner");
        
        return validationItem;
    }

    @Override
    public boolean save(ValidationItemModel org) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean delete(String id) {
        return false;
    }

    @Override
    public ValidationItemModel findById(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ValidationListModel findAll(int offset, int limit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ValidationListModel findByCertificateId(String certificateId, int offset, int limit) {
        // TODO Auto-generated method stub
        return null;
    }
    
}