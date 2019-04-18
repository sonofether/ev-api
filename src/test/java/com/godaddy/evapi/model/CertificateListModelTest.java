package com.godaddy.evapi.model;

import org.junit.Test;

public class CertificateListModelTest {

        @Test
        public void CertificateListTest() {
            CertificateListModel model = new CertificateListModel();
            model.setCertificates(null);
            model.setCount(0);
            model.setLimit(0);
            model.setOffset(0);
            
            assert(model.getCount() == 0);
            assert(model.getCertificates() == null);
            assert(model.getLimit() == 0);
            assert(model.getOffset() == 0);
            
        }
}
