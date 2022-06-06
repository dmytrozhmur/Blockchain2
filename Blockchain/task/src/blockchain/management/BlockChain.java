package blockchain.management;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BlockChain implements Iterable<Block>, Serializable {
    private Block head;
    private Block tail;
    private long size;
    private AtomicLong lastId = new AtomicLong(0);
    private AtomicInteger N = new AtomicInteger(0);
    private NavigableSet<Transaction> transactions = new TreeSet<>(Comparator.comparingLong(Transaction::getId));
    private ArrayList<MoneyHandler> moneyHandlers = new ArrayList<>();

    public BlockChain() {}

    public void checkValidityOf(Block beingChecked) throws InvalidObjectException {
        if(beingChecked.isProved(N.get()) && (tail == null
                || Objects.equals(tail.hash(), beingChecked.getPreviousHash()))) return;

        throw new InvalidObjectException("Block isn't valid");
    }

    public byte checkN(long generationTime) {
        if(generationTime > 1_000_000_000) {
            N.decrementAndGet();
            return -1;
        }

        if(generationTime < 10_000_000) {
            N.incrementAndGet();
            return 1;
        }

        return 0;
    }

    public void printBlocks(int bound) {
        if(0 > bound || bound > size)
            throw new IllegalArgumentException();

        int counter = 0;
        for (Block block: this) {
            block.print();
            if(counter++ == bound) return;
        }
    }

    public boolean addBlock(Block newBlock) {
        try {
            checkValidityOf(newBlock);
        } catch (InvalidObjectException e) {
            return false;
        }

        if(head == null) head = newBlock;
        else tail.next = newBlock;

        tail = newBlock;
        size++;

        return true;
    }

    public int getN() {
        return N.get();
    }

    public synchronized long getSize() {
        return size;
    }

    public synchronized void addTransanction(Transaction transaction) {
        if(transactions.isEmpty() || transaction.getId() > transactions.last().getId()
                && tail.checkMessages() || transaction.getId() > tail.getMessages().last().getId())
            transactions.add(transaction);
    }

    public List<Transaction> getLastTransaction() {
        List<Transaction> lastMessages = new ArrayList<>(transactions);
        transactions.clear();
        return lastMessages;
    }

    public long getId() {
        return lastId.incrementAndGet();
    }

    public void setMoneyHandlers(Collection<MoneyHandler> moneyHandlers) {
        this.moneyHandlers.addAll(moneyHandlers);
    }

    public ArrayList<MoneyHandler> getMoneyHandlers() {
        return (ArrayList<MoneyHandler>) moneyHandlers.clone();
    }

    public Block getTail() {
        return tail;
    }


    @Override
    public Iterator<Block> iterator() {
        return new BlockItr(head);
    }

    private class BlockItr implements Iterator<Block> {
        private Block curr;
        private Block lastReturned;

        public BlockItr(Block curr) {
            this.curr = curr;
        }

        @Override
        public boolean hasNext() {
            return curr != null;
        }

        @Override
        public Block next() {
            lastReturned = curr;
            curr = curr.next;
            return lastReturned;
        }
    }
}
