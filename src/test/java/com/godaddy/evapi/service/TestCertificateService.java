package com.godaddy.evapi.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.godaddy.evapi.model.CertificateListModel;
import com.godaddy.evapi.model.CertificateModel;

public class TestCertificateService implements ICertificateService{
    @Override
    public boolean save(CertificateModel org) {
        return true;
    }

    @Override
    public boolean delete(String id) {
        return true;
    }

    @Override
    public CertificateModel findById(String id) {
        return generateCertificate();
    }

    @Override
    public CertificateListModel findAll(int offset, int limit) {
        return generateCertificateList();
    }

    public static CertificateListModel generateCertificateList() {
        CertificateListModel certList = new CertificateListModel();
        
        certList.setCount(1);
        certList.setLimit(25);
        certList.setOffset(0);
        List<CertificateModel> certificates = new ArrayList<CertificateModel>();
        certificates.add(generateCertificate());
        certList.setCertificates(certificates);
        
        return certList;
    }
    
    public static CertificateModel generateCertificate() {
        CertificateModel cert = new CertificateModel();
        cert.setCommonName("example.com");
        cert.setExpiresDate(new Date());
        cert.setId(UUID.randomUUID());
        cert.setIssuedDate(new Date());
        cert.setOrganizationId(UUID.randomUUID());
        cert.setOrganizationName("Org Name");
        cert.setStatus(1);
        cert.setValidationLevel("1");
        return cert;
    }
}
