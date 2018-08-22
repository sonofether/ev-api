package com.godaddy.evapi.model;

import java.util.List;

public class ValidationListModel extends BaseListModel {
    private List<ValidationItemModel> validationItems;

    
    public List<ValidationItemModel> getValidationItems() {
        return validationItems;
    }

    
    public void setValidationItems(List<ValidationItemModel> validationItems) {
        this.validationItems = validationItems;
    }
    
}
