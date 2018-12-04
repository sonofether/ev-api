package com.godaddy.evapi.security;

import java.security.SecureRandom;
import java.util.Random;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.binary.Hex;

public class OneWayEncryption {
    private final static Random RANDOM = new SecureRandom();
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 512;
    
    public static final String DEFAULT_SALT = "GettingSaltyUpInHere";

    public static String HashValue(String value, byte[] salt) throws Exception {
        String hash = "";
        if(salt == null) {
            salt = DEFAULT_SALT.getBytes();
        }
        
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        PBEKeySpec keySpec = new PBEKeySpec(value.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKey key = keyFactory.generateSecret(keySpec);
        hash = Hex.encodeHexString(key.getEncoded());
        return hash;
    }
    
    public static byte[] getSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return salt;
    }
}
