package com.godaddy.evapi.model;

import java.util.UUID;

import org.junit.Test;

public class ValidationItemModelTest {

        @Test
        public void ValidationItemTest() {
            ValidationItemModel model = new ValidationItemModel("example.com", "myfile.txt", UUID.randomUUID(), "Owner", 1);
            
            assert(model.getId().toString().length() > 0);
            assert(model.getCertificateId().toString().length() > 0);
            assert(model.getItemUrl().equals("example.com"));
            assert(model.getFileName().equals("myfile.txt"));
            assert(model.getStatus() == 1);
            
        }
}
