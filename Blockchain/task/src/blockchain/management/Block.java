package blockchain.management;

import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.*;

import static blockchain.utils.Encryption.applySHA256;
import static blockchain.utils.Encryption.getRandomNumber;

public class Block implements Serializable {
    private float creationTime;
    private long id;
    private long timeStamp;
    private long magicNumber;
    private String previousHash;
    private String zeros;
    private byte zerosDiff;
    private String miner;
    private TreeSet<Transaction> messages
            = new TreeSet<>(Comparator.comparingLong(Transaction::getId));

    Block next;

    Block(String miner) {
        this.miner = miner;
    }

    public void init(int zerosQuantity, Block prev) {
        if(prev == null) {
            this.id = 1;
            this.previousHash = String.valueOf(0);
        } else {
            this.id = prev.id + 1;
            this.previousHash = prev.hash();
        }

        this.timeStamp = new Date().getTime();
        this.magicNumber = getRandomNumber(Long.MAX_VALUE);

        this.zeros = "0".repeat(Math.max(0, zerosQuantity));
    }

    public String hash() {
        return applySHA256(id + timeStamp + magicNumber + previousHash);
    }

    public boolean isProved(int zerosQuantityRequired) {
        return hash().startsWith("0".repeat(zerosQuantityRequired))
                && !hash().startsWith("0".repeat(zerosQuantityRequired + 1));
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public boolean checkMessages() {
        try {
            Signature sign = Signature.getInstance("SHA256withDSA");

            for (Transaction message: (Set<Transaction>) messages.clone()) {
                sign.initVerify(message.getKey());
                sign.update(message.toString().getBytes());
                if(!sign.verify(message.getSignature())) messages.remove(message);
            }
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            throw new RuntimeException("Unable to decrypt messages");
        }

        return messages.isEmpty();
    }

    public void setZerosDiff(byte zerosDiff) {
        this.zerosDiff = zerosDiff;
    }

    public TreeSet<Transaction> getMessages() {
        return (TreeSet<Transaction>) messages.clone();
    }

    public void setMessages(List<Transaction> messages) {
        this.messages.addAll(messages);
    }

    void print() {
        System.out.println("Block:\nCreated by " + miner);
        System.out.printf(miner + " gets 100 VC\n");
        System.out.println("Id: " + id);
        System.out.println("Timestamp: " + timeStamp);
        System.out.println("Magic number: " + magicNumber);
        System.out.println("Hash of the previous block:\n" + previousHash);
        System.out.println("Hash of the block:\n" + hash());

        String noMessagesWarning = checkMessages() ? "No transactions" : "";
        System.out.println("Block data:\n" + noMessagesWarning);
        for (Transaction message: messages) {
            System.out.println(message);
        }

        System.out.printf("Block was generated for %f seconds\n", creationTime);

        if(zerosDiff == 1) System.out.println("N was increased to " + (zeros.length() + 1));
        else if(zerosDiff == -1) System.out.println("N was decreased by 1");
        else System.out.println("N stays the same");
        System.out.println();
    }
    void setCreationTime(float creationTime) {
        this.creationTime = creationTime;
    }
}
