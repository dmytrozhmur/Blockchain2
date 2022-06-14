package blockchain.utils;

public class Constants {
    public static final long SECOND_TO_NANOS = 1_000_000_000;
    public static final long SECOND_TENTH_TO_NANOS = 100_000_000;
    public static final int KILOBYTE_TO_BYTE = 1024;
    public static final int SECOND_TO_MILLIS = 1000;
    public static final int CREATION_AWARD = 100;
    public static final int SHA_LENGTH = 64;
    public static final int BLOCKCHAIN_SIZE = 15;
    public static final int BLOCKCHAIN_PRINTED_SIZE = 15;
    public static final int NO_MESSAGES_BLOCKS_COUNT = 1;

    public static final String ZERO = String.valueOf(0);
    public static final String DSA = "DSA";
    public static final String SHA256 = "SHA-256";
    public static final String SHA_WITH_DSA = "SHA256withDSA";
    public static final String NO_TRANSACTIONS = "No transactions";
}
