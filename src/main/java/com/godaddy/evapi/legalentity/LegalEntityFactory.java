package com.godaddy.evapi.legalentity;


public class LegalEntityFactory {

    public ILegalEntity GewtLegalEntity(String countryCode) {
        if(countryCode.equals("")) {
            return new DefaultLegalEntity();
        } else if (countryCode.equals("")) {
            return new DefaultLegalEntity();
        }
        
        return new DefaultLegalEntity();
    }
}