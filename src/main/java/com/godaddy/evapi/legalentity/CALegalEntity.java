package com.godaddy.evapi.legalentity;

import java.util.Arrays;

public class CALegalEntity extends LegalEntity {
    public static String AULC                   = "aulc";
    public static String BCULC                  = "bculc";
    public static String COMMERCIAL_SOCIETY_FR  = "société commerciale canadienne";
    public static String COOPERATIVE            = "cooperative";
    public static String CO_OPERATIVE           = "co-operative";
    public static String COOP                   = "coop";
    public static String CO_OP                  = "co-op";
    public static String COOPERATIVE_FR         = "coopérative";
    public static String CORPORATION            = "corporation";
    public static String CORP_DOTS              = "corp.";
    public static String GENERAL_PARTNERSHIP    = "general partnership";    
    public static String INCORPORATED           = "incorporated";
    public static String INCORPORATED_FR        = "incorporée"; 
    public static String INC_DOTS               = "inc.";
    public static String JOINT_VENTURE          = "joint venture";
    public static String LIMITED                = "limited";
    public static String LIMITED_FR             = "limitée";
    public static String LIMITED_PARTNERSHIP    = "limited partnership";
    public static String LTD_DOTS               = "ltd.";
    public static String LTEE                   = "ltée";
    public static String NSULC                  = "nsulc";
    public static String SARF_DOTS              = "s.a.r.f.";
    public static String SCC_DOTS               = "s.c.c.";
    public static String SOCIETY_FR             = "société par actions de régime fédéral";
    public static String SOLE_PROPRIETOR        = "sole proprietorship";
    public static String UNITED                 = "united";
    public static String UNLIMITED_LIABILITY    = "unlimited liability corporation";

    public static String SP_DOTS                = " s.p.";
    public static String GP_DOTS                = " g.p."; 
    public static String LP_DOTS                = " l.p.";
    
    public static String[] validTypes = {AULC, BCULC, COMMERCIAL_SOCIETY_FR, COOPERATIVE, CO_OPERATIVE, COOP, CO_OP, COOPERATIVE_FR, CORPORATION, CORP_DOTS,
            GENERAL_PARTNERSHIP, INCORPORATED, INCORPORATED_FR, INC_DOTS, JOINT_VENTURE, LIMITED, LIMITED_FR, LIMITED_PARTNERSHIP, LTD_DOTS, LTEE, NSULC, SARF_DOTS,
            SCC_DOTS, SOCIETY_FR, SOLE_PROPRIETOR, UNITED, UNLIMITED_LIABILITY, SP_DOTS, GP_DOTS, LP_DOTS};
    
    public static String CORP                   = " corp";
    public static String INC                    = " inc";
    public static String POOL                   = " pool";
    public static String SARF                   = " sarf";
    public static String SCC                    = " scc";
    public static String SP                     = " sp";
    public static String GP                     = " gp"; 
    public static String LP                     = " lp";
    
    public static String[] validEndings = {CORP, INC, POOL, SARF, SCC, SP, GP, LP};

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
