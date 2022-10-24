package blockchain.utils;

import blockchain.exceptions.EncryptionException;
import blockchain.management.Transaction;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.TreeSet;

import static blockchain.utils.Constants.*;
import static blockchain.utils.Constants.KILOBYTE_TO_BYTE;

public class Encryption {
    private static KeyPairGenerator generator;
    private static Signature encryptionSign;
    private static Signature decryptionSign;

    static {
        try {
            encryptionSign = Signature.getInstance(SHA_WITH_DSA);
            decryptionSign = Signature.getInstance(SHA_WITH_DSA);
            generator = KeyPairGenerator.getInstance(DSA);
            generator.initialize(KILOBYTE_TO_BYTE);
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionException(e.getMessage());
        }
    }

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
            throw new EncryptionException(nae.getMessage());
        }
    }

    public static void decrypt(TreeSet<Transaction> messages, Transaction transaction) {
        try {
            decryptionSign.initVerify(transaction.getKey());
            decryptionSign.update(transaction.toString().getBytes());
            if(!decryptionSign.verify(transaction.getSignature())) messages.remove(transaction);
        } catch (SignatureException | InvalidKeyException e) {
            throw new EncryptionException("Unable to decrypt messages.");
        }
    }

    public static byte[] createSignature(String message, KeyPair pair) {
        try {
            encryptionSign.initSign(pair.getPrivate());
            encryptionSign.update(message.getBytes());
            return encryptionSign.sign();
        } catch (SignatureException | InvalidKeyException e) {
            throw new EncryptionException("Signature wasn't created");
        }
    }

    public static KeyPair getKeyPair() {
        return generator.generateKeyPair();
    }

    public static long getRandomNumber(long bound) {
        long number = (long) (Math.random() * bound);
        return Math.abs(number);
    }
}
