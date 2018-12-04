package com.godaddy.evapi.service;

import com.godaddy.evapi.model.ValidationInputModel;
import com.godaddy.evapi.model.ValidationItemModel;
import com.godaddy.evapi.model.ValidationListModel;

public interface IValidationService {
 // Create/Update
    boolean save(ValidationItemModel org);
    // Delete
    boolean delete(String id);
    // Read/Get
    ValidationItemModel findById(String id);
    ValidationListModel findAll(int offset, int limit);
    ValidationListModel findByCertificateId(String certificateId, int offset, int limit);
}
