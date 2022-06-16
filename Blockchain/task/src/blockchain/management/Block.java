package blockchain.management;

import blockchain.exceptions.EncryptionException;

import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static blockchain.utils.Constants.*;
import static blockchain.utils.Encryption.*;

public class Block implements Serializable {
    private long id;
    private long timeStamp;
    private long magicNumber;
    private String previousHash;
    private TreeSet<Transaction> messages
            = new TreeSet<>(Comparator.comparingLong(Transaction::getId));

    Block next;

    public void init(Block prev) {
        if(prev == null) {
            this.id = 1;
            this.previousHash = ZERO;
        } else {
            this.id = prev.id + 1;
            this.previousHash = prev.hash();
        }

        this.timeStamp = new Date().getTime();
        this.magicNumber = getRandomNumber(Long.MAX_VALUE);
    }

    public String hash() {
        return applySHA256(id + timeStamp + magicNumber + previousHash);
    }

    public boolean isProved(int zerosQuantityRequired) {
        String patternString = String.format("%s{%d}[^%s]{1}.{%d}",
                ZERO, zerosQuantityRequired, ZERO, SHA_LENGTH - zerosQuantityRequired - 1);
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(hash());
        return matcher.matches();
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public boolean checkMessages() {
        getMessages().forEach(transaction -> decrypt(messages, transaction));
        return messages.isEmpty();
    }

    public TreeSet<Transaction> getMessages() {
        return (TreeSet<Transaction>) messages.clone();
    }

    public void setMessages(List<Transaction> messages) {
        this.messages.addAll(messages);
    }

    long getId() {
        return id;
    }

    long getTimeStamp() {
        return timeStamp;
    }

    long getMagicNumber() {
        return magicNumber;
    }
}
