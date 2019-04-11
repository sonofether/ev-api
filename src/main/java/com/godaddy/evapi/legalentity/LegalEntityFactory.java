package com.godaddy.evapi.legalentity;


public class LegalEntityFactory {

    public static ILegalEntity GetLegalEntity(String countryCode) {
        if(countryCode.equals("CA")) {
            return new CALegalEntity();
        } else if (countryCode.equals("US")) {
            return new USLegalEntity();
        }
        
        return new DefaultLegalEntity();
    }
}