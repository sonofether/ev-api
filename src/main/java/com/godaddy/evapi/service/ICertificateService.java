package com.godaddy.evapi.service;

import com.godaddy.evapi.model.CertificateListModel;
import com.godaddy.evapi.model.CertificateModel;

public interface ICertificateService {
    CertificateModel save(CertificateModel certificate);
    boolean delete(String id);
    CertificateModel findById(String id);
    CertificateListModel findAll(int offset, int limit);
}
