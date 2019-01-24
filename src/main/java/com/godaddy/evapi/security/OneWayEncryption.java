package com.godaddy.evapi.security;

import java.security.SecureRandom;
import java.util.Random;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;

public class OneWayEncryption {
    @Value( "${encryption.salt.default}" )
    public void setDefaultSalt(String defaultSalt) {
        DEFAULT_SALT = defaultSalt;
    }
    
    private final static Random RANDOM = new SecureRandom();
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 512;
    
    public static String DEFAULT_SALT;

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
