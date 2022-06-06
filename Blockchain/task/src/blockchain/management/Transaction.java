package blockchain.management;

import java.security.PublicKey;

public class Transaction {
    private final long id;
    private final int coins;
    private final MoneyHandler sender;
    private final MoneyHandler receiver;
    private byte[] signature;
    private final PublicKey key;

    public Transaction(long id, int coins, MoneyHandler sender, MoneyHandler receiver, PublicKey key) {
        this.id = id;
        this.coins = coins;
        this.sender = sender;
        this.receiver = receiver;
        this.key = key;
    }

    public long getId() {
        return id;
    }

    public PublicKey getKey() {
        return key;
    }

    public int getCoins() {
        return coins;
    }

    public String getSender() {
        return sender.name();
    }

    public String getReceiver() {
        return receiver.name();
    }

    public byte[] getSignature() {
        return signature.clone();
    }

    void setSignature(byte[] signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return String.format("%s sent %d VC to %s", sender, coins, receiver);
    }

    @Override
    public int hashCode() {
        return coins;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(!obj.getClass().equals(getClass())) return false;
        return this == obj || this.id == ((Transaction)obj).id;
    }
}
