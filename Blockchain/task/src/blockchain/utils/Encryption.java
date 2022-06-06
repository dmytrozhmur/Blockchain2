package blockchain.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encryption {
    public static String applySHA256(String unencodedHash) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(
                    unencodedHash.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte el: hash) {
                String hex = Integer.toHexString(0xff & el);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException nae) {
            throw new RuntimeException();
        }
    }

    public static long getRandomNumber(long bound) {
        long number = (long) (Math.random() * bound);
        return Math.abs(number);
    }
}
