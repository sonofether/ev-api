package com.godaddy.evapi.model;

import java.util.ArrayList;
import java.util.List;

public class CertificateListModel extends BaseListModel {
    private List<CertificateModel> certificates;
    
    public CertificateListModel() {
        certificates = new ArrayList<CertificateModel>();
    }

    
    public List<CertificateModel> getCertificates() {
        return certificates;
    }

    
    public void setCertificates(List<CertificateModel> certificates) {
        this.certificates = certificates;
    }
    
}
