package blockchain.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static blockchain.utils.Constants.SHA256;
import static blockchain.utils.Constants.ZERO;

public class Encryption {
    public static String applySHA256(String unencodedHash) {
        try {
            MessageDigest digest = MessageDigest.getInstance(SHA256);
            byte[] hash = digest.digest(
                    unencodedHash.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte el: hash) {
                String hex = Integer.toHexString(0xff & el);
                if (hex.length() == 1) hexString.append(ZERO);
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
