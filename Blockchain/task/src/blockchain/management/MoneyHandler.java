package blockchain.management;

import blockchain.exceptions.EncryptionException;
import blockchain.exceptions.IllegalTransactionArgumentException;

import java.security.*;
import java.util.*;

import static blockchain.utils.Constants.*;
import static blockchain.utils.Encryption.getRandomNumber;

public abstract class MoneyHandler extends Thread {
    protected String nickName;
    protected final BlockChain blockChain;
    protected int moneyForCreation = 100;
    private final LinkedList<Transaction> transactions = new LinkedList<>();

    public MoneyHandler(String name, BlockChain blockChain) {
        this.nickName = name;
        this.blockChain = blockChain;
    }

    public Transaction createTransaction(String[] data) throws IllegalTransactionArgumentException {
        MoneyHandler receiver;
        if(data.length != 2 || ((receiver = getReceiver(data[1])) == null))
            throw new IllegalTransactionArgumentException("Invalid input");
        int sum = Integer.parseInt(data[0]);

        checkMoney(sum);

        KeyPair pair = getKeyPair();
        Transaction transaction
                = new Transaction(blockChain.getId(), sum, this, receiver, pair.getPublic());
        transaction.setSignature(createSignature(transaction.toString(), pair));

        this.transactions.add(transaction);
        receiver.transactions.add(transaction);
        blockChain.addTransaction(transaction);
        return transaction;
    }

    public String name() {
        return nickName;
    }

    protected KeyPair getKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(DSA);
            generator.initialize(KILOBYTE_TO_BYTE);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionException("Keys weren't generated");
        }
    }

    protected int getMoneyHeld() {
        int moneyHeld = moneyForCreation;

        for (Transaction transaction : (LinkedList<Transaction>) transactions.clone()) {
            if(transaction.getSender().equals(nickName))
                moneyHeld -= transaction.getCoins();
            if(transaction.getReceiver().equals(nickName))
                moneyHeld += transaction.getCoins();
        }

        return moneyHeld;
    }

    protected byte[] createSignature(String message, KeyPair pair) {
        try {
            Signature sign = Signature.getInstance(SHA_WITH_DSA);
            sign.initSign(pair.getPrivate());
            sign.update(message.getBytes());
            return sign.sign();
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            throw new EncryptionException("Signature wasn't created");
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
                    throw new IllegalTransactionArgumentException("You are trying to send your money to yourself");
                else
                    return handler;
            }
        }
        return null;
    }

    private void checkMoney(int cancelling) {
        int moneyHeld = getMoneyHeld();
        if(moneyHeld < cancelling) throw new IllegalTransactionArgumentException("Not enough money");
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
