package blockchain.management;

import java.security.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static blockchain.utils.Encryption.getRandomNumber;

public abstract class MoneyHandler extends Thread {
    protected String nickName;
    protected final BlockChain blockChain;
    protected int moneyForCreation = 100;
    private final List<Transaction> transactions = new LinkedList<>();

    public MoneyHandler(String name, BlockChain blockChain) {
        this.nickName = name;
        this.blockChain = blockChain;
    }

    public Transaction createTransaction(String[] data) throws IllegalArgumentException {
        MoneyHandler receiver;
        if(data.length != 2 || ((receiver = getReceiver(data[1])) == null))
            throw new IllegalArgumentException("Invalid input");
        int sum = Integer.parseInt(data[0]);
        checkMoney(sum);

        KeyPair pair = getKeyPair();
        Transaction transaction
                = new Transaction(blockChain.getId(), sum, this, receiver, pair.getPublic());
        transaction.setSignature(createSignature(transaction.toString(), pair));

        this.transactions.add(transaction);
        receiver.transactions.add(transaction);
        blockChain.addTransanction(transaction);
        return transaction;
    }

    public String name() {
        return nickName;
    }

    protected KeyPair getKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("DSA");
            generator.initialize(16);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Keys weren't generated");
        }
    }

    protected int getMoneyHeld() {
        int moneyHeld = moneyForCreation;

        for (Transaction transaction : transactions) {
            if(transaction.getSender().equals(nickName))
                moneyHeld -= transaction.getCoins();
            if(transaction.getReceiver().equals(nickName))
                moneyHeld += transaction.getCoins();
        }
        return moneyHeld;
    }

    protected byte[] createSignature(String message, KeyPair pair) {
        try {
            Signature sign = Signature.getInstance("SHA256withDSA");
            sign.initSign(pair.getPrivate());
            sign.update(message.getBytes());
            return sign.sign();
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            throw new RuntimeException("Signature wasn't created");
        }
    }

    protected MoneyHandler getRandomReceiver() {
        ArrayList<MoneyHandler> handlers = blockChain.getMoneyHandlers();
        int index = (int) getRandomNumber(handlers.size());
        return handlers.get(index);
    }

    private MoneyHandler getReceiver(String name) {
        for (MoneyHandler handler : blockChain.getMoneyHandlers()) {
            if(handler.nickName.equals(name)) {
                if(handler.equals(this))
                    throw new IllegalArgumentException("You are trying to send your money to yourself");
                else
                    return handler;
            }
        }
        return null;
    }

    private void checkMoney(int cancelled) {
        int moneyHeld = getMoneyHeld();
        if(moneyHeld < cancelled) throw new IllegalArgumentException("Not enough money");
    }

    @Override
    public String toString() {
        return nickName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoneyHandler handler = (MoneyHandler) o;
        return nickName.equals(handler.nickName) && getId() == handler.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickName);
    }
}
