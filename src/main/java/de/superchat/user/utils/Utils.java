package de.superchat.user.utils;

import java.security.SecureRandom;

public class Utils {

    private Utils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Random a password salt to be used with bcrypt
     *
     * @return salt as array of byte
     */
    public static byte[] randomBcryptSalt() {
        SecureRandom random = new SecureRandom();
        byte salt[] = new byte[16];
        random.nextBytes(salt);
        return salt;
    }
}