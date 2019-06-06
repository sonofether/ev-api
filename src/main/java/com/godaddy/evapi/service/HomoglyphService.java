package com.godaddy.evapi.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.godaddy.evapi.model.TopSiteListModel;
import com.godaddy.evapi.model.TopSiteModel;

@Service
public class HomoglyphService {
    @Autowired
    ITopSitesService topSitesService;
    
    @Autowired
    ILoggingService loggingService;
    
    private static final List<Set<Integer>> homoglyphSet = new ArrayList<Set<Integer>>();
    private final CachingLookup cache = new CachingLookup();
    
    public boolean containsMixedAlphabets(String evaluateStr) {
        boolean mixedCharacters = false;
        boolean hasLetter = false;
        boolean containsLatin = false;
        boolean containsArabic = false;
        boolean containsArmenian = false;
        boolean containsBalinese = false;
        boolean containsCyrillic = false;
        boolean containsGreek = false;
        boolean containsHebrew = false;
        boolean containsMongolian = false;
        boolean containsTibetan = false;
        boolean containsOther = false;
        
        for(char c : evaluateStr.toCharArray() ) {
            // We only care about letters for this test.
            if(!Character.isLetter(c))
                continue;

            if(isLatinLetter(c))
                containsLatin = true;
            else if(isArabicLetter(c)) 
                containsArabic = true;
            else if(isArmenianLetter(c))
                containsArmenian = true;
            else if(isBalineseLetter(c))
                containsBalinese = true;
            else if(isCyrillicLetter(c))
                containsCyrillic = true;
            else if(isGreekLetter(c))
                containsGreek = true;
            else if(isHebrewLetter(c))
                containsHebrew = true;
            else if(isMongolianLetter(c))
                containsMongolian = true;
            else if(isTibetanLetter(c))
                containsTibetan = true;            
            else 
                containsOther = true;
        }
        
        if(containsLatin) {
            hasLetter = true;
        }
        
        if(containsArabic) {
            mixedCharacters = hasLetter ? true : false;
            hasLetter = true;
        }
        
        if(containsArmenian) {
            mixedCharacters = hasLetter ? true : false;
            hasLetter = true;
        }
        
        if(containsBalinese) {
            mixedCharacters = hasLetter ? true : false;
            hasLetter = true;
        }

        if(containsCyrillic) {
            mixedCharacters = hasLetter ? true : false;
            hasLetter = true;
        }
                
        if(containsGreek) {
            mixedCharacters = hasLetter ? true : false;
            hasLetter = true;
        }
        
        if(containsHebrew) {
            mixedCharacters = hasLetter ? true : false;
            hasLetter = true;
        }
        
        if(containsMongolian) {
            mixedCharacters = hasLetter ? true : false;
            hasLetter = true;
        }
        
        if(containsTibetan) {
            mixedCharacters = hasLetter ? true : false;
            hasLetter = true;
        }
        
        if(containsOther) {
            mixedCharacters = hasLetter ? true : false;
            hasLetter = true;
        }
        
        return mixedCharacters;
    }

    public String convertHomoglyphs(String source) {
        if(source.matches("[\\p{Punct}\\p{Space}\\p{IsLatin}]+$")) {
            return source;
        }

        List<List<Integer>> homoglyphs = buildHomoglyphList();
        StringBuilder convertedDomain = new StringBuilder(source.length());
        for(int ii = 0; ii < source.length(); ii++) {
            char currentChar = source.charAt(ii);
            Character.UnicodeBlock character = Character.UnicodeBlock.of(currentChar);
            // TODO: Test This
            if(isLatinLetter(currentChar) || Character.isDigit(currentChar)) {
                convertedDomain.append(currentChar);
                continue;
            }

            // Try to convert the character
            Integer newCharVal = lookup(homoglyphs, currentChar);
            if(newCharVal >= 0) {
                // Store the lower case value of the converted character
                convertedDomain.append(Character.toLowerCase((char)newCharVal.intValue()));
            } else {
                convertedDomain.append(currentChar);
            }
        }
        
        return convertedDomain.toString();
    }

    public List<String> searchForTopSites(String domain) {
        List<String> concerns = new ArrayList<String>();
        TopSiteListModel topSites = topSitesService.findAll(0, 500);
        try {
            buildLargeHomoglyphList();
            for(TopSiteModel site : topSites.getTopSites()) {
                List<SearchResult> results = search(domain, site.getUrl());
                if(!results.isEmpty()) {
                    for(SearchResult result : results) {
                        concerns.add(result.toString());
                    }
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }        
        
        return concerns;
    }
    
    
    // PRIVATE METHODS AND HELPERS
    
    private static boolean isLatinLetter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }
        
    private static boolean isArabicLetter(char c) {
        return Character.UnicodeBlock.ARABIC.equals(Character.UnicodeBlock.of(c));
    }
    
    private static boolean isArmenianLetter(char c) {
        return Character.UnicodeBlock.ARMENIAN.equals(Character.UnicodeBlock.of(c));
    }
    
    private static boolean isBalineseLetter(char c) {
        return Character.UnicodeBlock.BALINESE.equals(Character.UnicodeBlock.of(c));
    }

    private static boolean isCyrillicLetter(char c) {
        return Character.UnicodeBlock.CYRILLIC.equals(Character.UnicodeBlock.of(c));
    }

    private static boolean isGreekLetter(char c) {
        return Character.UnicodeBlock.GREEK.equals(Character.UnicodeBlock.of(c));
    }
    
    private static boolean isHebrewLetter(char c) {
        return Character.UnicodeBlock.HEBREW.equals(Character.UnicodeBlock.of(c));
    }

    private static boolean isMongolianLetter(char c) {
        return Character.UnicodeBlock.MONGOLIAN.equals(Character.UnicodeBlock.of(c));
    }

    private static boolean isTibetanLetter(char c) {
        return Character.UnicodeBlock.TIBETAN.equals(Character.UnicodeBlock.of(c));
    }

    private static boolean isOtherLetter(char c) {
        return Character.isLetter(c);
    }

    private static List<List<Integer>> buildHomoglyphList() {
        // Build our Homoglyph list from a bunch of sets of integers
        List<List<Integer>> homoglyphs = new ArrayList<List<Integer>>();
        
        homoglyphs.add(Arrays.asList(new Integer[] {32,160,5760,8192,8193,8194,8195,8196,8197,8198,8199,8200,8201,8202,8232,8233,8239,8287} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {33,451,11601,65281}));
        homoglyphs.add(Arrays.asList(new Integer[] {36,65284} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {37,65285} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {38,42872,65286} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {39,96,180,697,699,700,701,702,712,714,715,756,884,900,1370,1373,1497,1523,2036,2037,5194,5836,8125,8127,8175,8189,8190,8216,8217,8219,8242,8245,42892,65287,65344,94033,94034} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {40,10088,10098,12308,64830,65288,65339} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {41,10089,10099,12309,64831,65289,65341} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {42,1645,8270,8727,65290,66335} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {43,5869,10133,65291,66203} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {44,184,1549,1643,8218,42233,65292} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {45,727,1748,8208,8209,8210,8211,8259,8722,10134,11450,65112} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {46,1632,1776,1793,1794,8228,42232,42510,65294,68176,119149} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {47,5941,8257,8260,8725,9585,10187,10744,11462,12035,12339,12494,12755,20031,65295,119354} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {79,111,48,927,959,963,1054,1086,1365,1413,1505,1607,1637,1726,1729,1749,1781,1984,2406,2534,2662,2790,2848,2918,3046,3074,3174,3202,3302,3330,3360,3430,3458,3664,3792,4125,4160,4351,4816,7439,7441,8500,11422,11423,11604,12295,42227,43837,64422,64423,64424,64425,64426,64427,64428,64429,65257,65258,65259,65260,65296,65327,65359,66194,66219,66564,66604,66754,66794,66838,70864,71861,71880,71895,71904,119822,119848,119874,119900,119926,119952,119978,120030,120056,120082,120108,120134,120160,120186,120212,120238,120264,120290,120316,120342,120368,120394,120420,120446,120472,120502,120528,120532,120560,120586,120590,120618,120644,120648,120676,120702,120706,120734,120760,120764,120782,120792,120802,120812,120822,126500,126564,126596} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {73,49,108,124,406,448,921,1030,1216,1472,1493,1503,1575,1633,1777,1994,5825,8464,8465,8467,8544,8572,8739,9213,11410,11599,42226,65165,65166,65297,65321,65356,65512,66186,66313,66336,93992,119816,119845,119868,119897,119920,119949,120001,120024,120053,120105,120128,120157,120180,120209,120232,120261,120284,120313,120336,120365,120388,120417,120440,120469,120496,120554,120612,120670,120728,120783,120793,120803,120813,120823,125127,126464,126592} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {50,423,1000,5311,42564,42735,42842,65298,120784,120794,120804,120814,120824} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {51,439,540,1047,1248,11468,42858,42923,65299,71882,94011,119302,120785,120795,120805,120815,120825} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {52,5070,65300,71855,120786,120796,120806,120816,120826} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {53,444,65301,71867,120787,120797,120807,120817,120827} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {54,1073,5102,11474,65302,71893,120788,120798,120808,120818,120828} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {55,65303,66770,71878,119314,120789,120799,120809,120819,120829} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {56,546,547,2538,2666,2819,65304,66330,120790,120800,120810,120820,120830,125131} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {57,2541,2663,2920,3437,11466,42862,65305,71852,71884,71894,120791,120801,120811,120821,120831} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {58,720,760,1417,1475,1795,1796,2307,2691,5868,6147,6153,8282,8758,42237,42889,65072,65306} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {59,894,65307} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {60,706,5176,5810,8249,10094,65308,119350} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {61,5120,11840,12448,42239,65309} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {62,707,5171,8250,10095,65310,94015,119351} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {63,577,660,2429,5038,42731,65311} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {64,65312} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {65,913,1040,5034,5573,7424,42222,43898,65313,66208,94016,119808,119860,119912,119964,120016,120068,120120,120172,120224,120276,120328,120380,120432,120488,120546,120604,120662,120720} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {66,665,914,1042,1074,5108,5116,5623,5842,8492,42192,42932,65314,66178,66209,66305,119809,119861,119913,120017,120069,120121,120173,120225,120277,120329,120381,120433,120489,120547,120605,120663,120721} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {67,1017,1057,5087,8450,8493,8557,11428,42202,65315,66210,66306,66581,66844,71913,71922,119810,119862,119914,119966,120018,120174,120226,120278,120330,120382,120434,128844} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {68,5024,5598,5610,7429,8517,8558,42195,43888,65316,119811,119863,119915,119967,120019,120071,120123,120175,120227,120279,120331,120383,120435} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {69,917,1045,5036,7431,8496,8959,11577,42224,43900,65317,66182,71846,71854,119812,119864,119916,120020,120072,120124,120176,120228,120280,120332,120384,120436,120492,120550,120608,120666,120724} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {70,988,5556,8497,42205,42904,65318,66183,66213,66853,71842,71874,119315,119813,119865,119917,120021,120073,120125,120177,120229,120281,120333,120385,120437,120778} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {71,610,1292,1293,5056,5107,5115,42198,43920,65319,119814,119866,119918,119970,120022,120074,120126,120178,120230,120282,120334,120386,120438} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {72,668,919,1053,1085,5051,5500,8459,8460,8461,11406,42215,43915,65320,66255,119815,119867,119919,120023,120179,120231,120283,120335,120387,120439,120494,120552,120610,120668,120726} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {74,895,1032,5035,5261,7434,42201,42930,43899,65322,119817,119869,119921,119973,120025,120077,120129,120181,120233,120285,120337,120389,120441} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {75,922,1050,5094,5845,8490,11412,42199,65323,66840,119818,119870,119922,119974,120026,120078,120130,120182,120234,120286,120338,120390,120442,120497,120555,120613,120671,120729} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {76,671,5086,5290,8466,8556,11472,11473,42209,43950,65324,66587,66627,66854,71843,71858,93974,119338,119819,119871,119923,120027,120079,120131,120183,120235,120287,120339,120391,120443} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {77,924,1018,1052,5047,5616,5846,8499,8559,11416,42207,65325,66224,66321,119820,119872,119924,120028,120080,120132,120184,120236,120288,120340,120392,120444,120499,120557,120615,120673,120731} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {78,628,925,8469,11418,42208,65326,66835,119821,119873,119925,119977,120029,120081,120185,120237,120289,120341,120393,120445,120500,120558,120616,120674,120732} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {80,929,1056,5090,5229,7448,7465,8473,11426,42193,43954,65328,66197,119823,119875,119927,119979,120031,120083,120187,120239,120291,120343,120395,120447,120504,120562,120620,120678,120736} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {81,8474,11605,65329,119824,119876,119928,119980,120032,120084,120188,120240,120292,120344,120396,120448} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {82,422,640,5025,5074,5511,5809,8475,8476,8477,42211,43889,43938,65330,66740,94005,119318,119825,119877,119929,120033,120189,120241,120293,120345,120397,120449} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {83,1029,1359,5077,5082,42210,65331,66198,66592,94010,119826,119878,119930,119982,120034,120086,120138,120190,120242,120294,120346,120398,120450} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {84,932,964,1058,1090,5026,7451,8868,10201,11430,42196,43890,65332,66199,66225,66325,71868,93962,119827,119879,119931,119983,120035,120087,120139,120191,120243,120295,120347,120399,120451,120507,120533,120565,120591,120623,120649,120681,120707,120739,120765,128872} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {85,1357,4608,5196,8746,8899,42228,65333,66766,71864,94018,119828,119880,119932,119984,120036,120088,120140,120192,120244,120296,120348,120400,120452} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {86,1140,1639,1783,5081,5167,8548,11576,42214,42719,65334,66845,71840,93960,119309,119829,119881,119933,119985,120037,120089,120141,120193,120245,120297,120349,120401,120453} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {87,1308,5043,5076,42218,65335,71910,71919,119830,119882,119934,119986,120038,120090,120142,120194,120246,120298,120350,120402,120454} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {88,935,1061,5741,5815,8553,9587,11436,11613,42219,42931,65336,66192,66228,66327,66338,66855,71916,119831,119883,119935,119987,120039,120091,120143,120195,120247,120299,120351,120403,120455,120510,120568,120626,120684,120742} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {89,933,978,1059,1198,5033,5053,11432,42220,65337,66226,71844,94019,119832,119884,119936,119988,120040,120092,120144,120196,120248,120300,120352,120404,120456,120508,120566,120624,120682,120740} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {90,918,5059,8484,8488,42204,65338,66293,71849,71909,119833,119885,119937,119989,120041,120197,120249,120301,120353,120405,120457,120493,120551,120609,120667,120725} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {92,8726,10189,10741,10745,12034,12756,20022,65128,65340,119311,119355} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {94,708,710} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {95,2042,65101,65102,65103,65343} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {97,593,945,1072,9082,65345,119834,119886,119938,119990,120042,120094,120146,120198,120250,120302,120354,120406,120458,120514,120572,120630,120688,120746} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {98,388,1068,5071,5551,65346,119835,119887,119939,119991,120043,120095,120147,120199,120251,120303,120355,120407,120459} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {99,1010,1089,7428,8573,11429,43951,65347,66621,119836,119888,119940,119992,120044,120096,120148,120200,120252,120304,120356,120408,120460} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {100,1281,5095,5231,8518,8574,42194,65348,119837,119889,119941,119993,120045,120097,120149,120201,120253,120305,120357,120409,120461} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {101,1077,1213,8494,8495,8519,43826,65349,119838,119890,119942,120046,120098,120150,120202,120254,120306,120358,120410,120462} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {102,383,989,1412,7837,42905,43829,65350,119839,119891,119943,119995,120047,120099,120151,120203,120255,120307,120359,120411,120463,120779} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {103,397,609,1409,7555,8458,65351,119840,119892,119944,120048,120100,120152,120204,120256,120308,120360,120412,120464} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {104,1211,1392,5058,8462,65352,119841,119945,119997,120049,120101,120153,120205,120257,120309,120361,120413,120465} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {105,305,617,618,731,890,953,1110,1231,5029,8126,8505,8520,8560,9075,42567,43893,65353,71875,119842,119894,119946,119998,120050,120102,120154,120206,120258,120310,120362,120414,120466,120484,120522,120580,120638,120696,120754} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {106,1011,1112,8521,65354,119843,119895,119947,119999,120051,120103,120155,120207,120259,120311,120363,120415,120467} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {107,65355,119844,119896,119948,120000,120052,120104,120156,120208,120260,120312,120364,120416,120468} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {109,65357} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {110,1400,1404,65358,119847,119899,119951,120003,120055,120107,120159,120211,120263,120315,120367,120419,120471} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {112,961,1009,1088,9076,11427,65360,119849,119901,119953,120005,120057,120109,120161,120213,120265,120317,120369,120421,120473,120530,120544,120588,120602,120646,120660,120704,120718,120762,120776} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {113,1307,1379,1382,65361,119850,119902,119954,120006,120058,120110,120162,120214,120266,120318,120370,120422,120474} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {114,1075,7462,11397,43847,43848,43905,65362,119851,119903,119955,120007,120059,120111,120163,120215,120267,120319,120371,120423,120475} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {115,445,1109,42801,43946,65363,66632,71873,119852,119904,119956,120008,120060,120112,120164,120216,120268,120320,120372,120424,120476} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {116,65364,119853,119905,119957,120009,120061,120113,120165,120217,120269,120321,120373,120425,120477} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {117,651,965,1405,7452,42911,43854,43858,65365,66806,71896,119854,119906,119958,120010,120062,120114,120166,120218,120270,120322,120374,120426,120478,120534,120592,120650,120708,120766} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {118,957,1141,1496,7456,8564,8744,8897,43945,65366,71430,71872,119855,119907,119959,120011,120063,120115,120167,120219,120271,120323,120375,120427,120479,120526,120584,120642,120700,120758} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {119,623,1121,1309,1377,7457,43907,65367,71434,71438,71439,119856,119908,119960,120012,120064,120116,120168,120220,120272,120324,120376,120428,120480} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {120,215,1093,5441,5501,5742,8569,10539,10540,10799,65368,119857,119909,119961,120013,120065,120117,120169,120221,120273,120325,120377,120429,120481} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {121,611,655,947,1091,1199,4327,7564,7935,8509,43866,65369,71900,119858,119910,119962,120014,120066,120118,120170,120222,120274,120326,120378,120430,120482,120516,120574,120632,120690,120748} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {122,7458,43923,65370,71876,119859,119911,119963,120015,120067,120119,120171,120223,120275,120327,120379,120431,120483} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {123,10100,65371,119060} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {125,10101,65373} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {126,732,8128,8275,8764} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {163,8356} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {169,9400} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {174,9415} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {175,713,8254,9620,65097,65098,65099,65100,65507} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {176,730,8728,9675,9702,11824} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {181,956,120525,120583,120641,120699,120757} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {182,11839} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {183,903,5159,5867,8226,8231,8729,8901,11825,12539,42895,65381,65793} ) );
        homoglyphs.add(Arrays.asList(new Integer[] {186,7506,8304} ) );
        
        return homoglyphs;
    }
    
    private Integer lookup(List<List<Integer>> homoglyphs, char c) {
        int charInt = Character.toLowerCase(c);
        Integer returnValue = -1;
        for (List<Integer> thisList : homoglyphs){
            if (thisList.contains(charInt)){
                returnValue = thisList.get(0);
                break;
            }
        }
        
        return returnValue;
    }
    
    private static void buildLargeHomoglyphList() throws IOException {
        // Build our Homoglyph list from a bunch of sets of integers
        homoglyphSet.addAll(loadFile());
    }
    
    private List<SearchResult> search(String source, String target) {
        final List<SearchResult> results = new ArrayList<SearchResult>();
        final CodePoints textCodepoints = new CodePoints(source);
        final CodePoints targetCodepoints = new CodePoints(target);
        
        int lastIndex = textCodepoints.getLength() - targetCodepoints.getLength();
        for (int i = 0; i <= lastIndex; i++) {
            if (hasWordAtIndex(textCodepoints, targetCodepoints, i)) {
                results.add(new SearchResult(i, textCodepoints.subStringAt(i, targetCodepoints.getLength()), targetCodepoints.getText()));
            }
        }
        
        return results;
    }
    
    private boolean hasWordAtIndex(final CodePoints text, final CodePoints targetWord, final int index){
        for (int i=0; i<targetWord.getLength(); i++){
            final int targetCharLower = Character.toLowerCase(targetWord.getValue(i));
            final int targetCharUpper = Character.toUpperCase(targetWord.getValue(i));
            final int textChar = text.getValue(index + i);
            if (!checkForHomoglyphs(targetCharLower, textChar) && !checkForHomoglyphs(targetCharUpper, textChar)){
                return false;
            }
        }
        return true;
    }
    
    private boolean checkForHomoglyphs(final int cp1, final int cp2) {
        final Set<Integer> cp1Set = cache.lookup(cp1);
        return cp1Set.contains(cp2);
    }
    
    private static List<Set<Integer>> loadFile() throws IOException {
        final List<Set<Integer>> homoglyphs = new ArrayList<Set<Integer>>();

        try (final BufferedReader bufferedReader = new BufferedReader(new FileReader("/Users/asink/char_codes.txt"))) {
            String line;
            while((line = bufferedReader.readLine()) != null){
                line = line.trim();
                if (line.startsWith("#") || line.length() == 0){
                    continue;
                }
                final Set<Integer> set = new HashSet<Integer>();
                for (String charCode : line.split(",")) {
                    try {
                        set.add(Integer.parseInt(charCode, 16));
                    } catch (NumberFormatException ex){
                        // ignore badly formatted lines
                    }
                }
                homoglyphs.add(set);
            }
        }
        
        return homoglyphs;
    }
    
    public static class SearchResult {
        public SearchResult(final int index, final String match, final String word){
            this.index = index;
            this.match = match;
            this.word = word;
        }
        public int index;
        public String match;
        public String word;

        @Override
        public String toString() {
            return String.format("'%s' at position %s matches '%s'", match, index, word);
        }
    }

    public static class CodePoints{
        private final Integer[] codepoints;
        private final String text;

        public CodePoints(String text){
            this.text = text;

            final List<Integer> codepointList = new ArrayList<>();
            int codepoint;
            for (int offset = 0; offset < text.length(); ) {
                codepointList.add(codepoint = text.codePointAt(offset));
                offset += Character.charCount(codepoint);
            }
            codepoints = codepointList.toArray(new Integer[0]);
        }

        public int getValue(int i) {
            return codepoints[i];
        }

        public int getLength(){
            return codepoints.length;
        }

        public String getText(){
            return text;
        }

        public String subStringAt(final int s, final int l){
            final StringBuilder sb = new StringBuilder(l);
            for (int i=0; i<l; i++){
                sb.appendCodePoint(this.codepoints[s+i]);
            }
            return sb.toString();
        }
    }

    public static class CachingLookup{
        private final Map<Integer, Set<Integer>> lookup = new HashMap<Integer, Set<Integer>>();

        public Set<Integer> lookup(final int cp){
            Set<Integer> s = lookup.get(cp);
            if (s == null){
                for (Set<Integer> thisSet : homoglyphSet){
                    if (thisSet.contains(cp)){
                        s = thisSet;
                        break;
                    }
                }
                if (s == null){
                    s = new HashSet<Integer>();
                    s.add(cp);
                }
                lookup.put(cp, s);
            }
            return s;
        }
    }
    
}
