package com.example.listadeeventos.db;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import android.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtils {

    private PasswordUtils(){}

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATION_COUNT = 120_000;
    private static final int KEY_LENGTH = 265;

    public static byte[] generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    public static byte[] hash(char[] password, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATION_COUNT, KEY_LENGTH);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] hash = skf.generateSecret(spec).getEncoded();

        Arrays.fill(password, '\u0000');
        return hash;
    }

    public static boolean verify(char[] password, byte[] salt, byte[] expectedHash) throws Exception {
        byte[] candidate = hash(password, salt);
        boolean ok = MessageDigest.isEqual(candidate, expectedHash);
        Arrays.fill(candidate, (byte) 0 );
        Arrays.fill(password, '\u0000');
        return ok;
    }

    public static String toBase64(byte[] data) {
        return Base64.encodeToString(data, Base64.NO_WRAP);
    }

    public static byte[] fromBase64(String s) {
        return Base64.decode(s, Base64.NO_WRAP);
    }
}