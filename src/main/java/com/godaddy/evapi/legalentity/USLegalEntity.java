package com.godaddy.evapi.legalentity;

import java.util.Arrays;
import java.util.List;

public class USLegalEntity extends LegalEntity{
    // We probably don't care about case 
    public static String ASSOCIATION    = "association";
    public static String BANK           = "bank";
    public static String BANKING        = "banking";
    public static String BANKERS        = "bankers";
    public static String CHURCH         = "church";
    public static String CODOTS         = "co.";
    public static String COLLEGE        = "college";
    public static String COMPANY        = "company";
    public static String CORPDOTS       = "corp.";
    public static String CORPORATION    = "corporation";
    public static String CREDITUNION    = "credit union";
    public static String FCU            = "fcu";
    public static String FCUDOTS        = "f.c.u."; 
    public static String FOUNDATION     = "foundation";
    public static String FUND           = "fund";
    public static String INCDOTS        = "inc."; 
    public static String INCORPORATED   = "incorporated";
    public static String INSTITUTE      = "institute";
    public static String LCDOTS         = "l.c.";
    public static String LIMITED        = "limited";
    public static String LLCDOTS        = "l.l.c";
    public static String LLP            = "llp";
    public static String LLPDOTS        = "l.l.p";
    public static String LTDDOTS        = "ltd.";
    public static String NATIONAL       = "national";
    public static String PARTNERSHIP    = "partnership";
    public static String SOCIETY        = "society";
    public static String SYNDICATE      = "syndicate";
    public static String TRUST          = "trust";
    public static String UNION          = "union";
    public static String UNIVERSITY     = "university";
    
    public static String[] validTypes = {ASSOCIATION, BANK, BANKING, BANKERS, CHURCH, CODOTS, COLLEGE, COMPANY, CORPDOTS, CORPORATION, CREDITUNION, FCU, FCUDOTS, 
            FOUNDATION, FUND, INCDOTS, INCORPORATED, INSTITUTE, LCDOTS, LIMITED, LLCDOTS, LLP, LLPDOTS, LTDDOTS, NATIONAL, PARTNERSHIP, SOCIETY, SYNDICATE, TRUST, 
            UNION, UNIVERSITY};
    
    public static String CO             = " co";
    public static String CORP           = " corp";
    public static String INC            = " inc"; 
    public static String LC             = " lc";
    public static String LLC            = " llc";
    public static String LTD            = " ltd";
    
    public static String[] validEndings = {CO, CORP, INC, LC, LLC, LTD};

    // TODO: Do we need to break this down by state?
    @Override
    public boolean validate(String organizationName) {
        boolean isValid = false;
        if(organizationName != null) {
            String orgName = organizationName.trim().toLowerCase();
            isValid = Arrays.stream(validTypes).parallel().anyMatch(orgName::contains);
            if(isValid == false) {
                isValid = Arrays.stream(validEndings).parallel().anyMatch(orgName::endsWith);
            }
        }
        
        return isValid;
    }
    
}
