package com.godaddy.evapi.legalentity;


public class DefaultLegalEntity extends LegalEntity {
    @Override
    public boolean validate(String organizationName) {
        return true;
    }

}
