package blockchain.management;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static blockchain.utils.Constants.*;

public class BlockChain implements Iterable<Block>, Serializable {
    private Block head;
    private Block tail;
    private long size;
    private AtomicLong lastBlockId = new AtomicLong(Long.parseLong(ZERO));
    private AtomicInteger N = new AtomicInteger(Integer.parseInt(ZERO));
    private NavigableSet<Transaction> transactions = new TreeSet<>(Comparator.comparingLong(Transaction::getId));
    private ArrayList<MoneyHandler> moneyHandlers = new ArrayList<>();

    public boolean isInvalid(Block beingChecked) {
        return !beingChecked.isProved(getN()) || (tail != null
                && !Objects.equals(tail.hash(), beingChecked.getPreviousHash()));
    }

    public byte checkN(long generationTime) {
        if(generationTime > SECOND_TO_NANOS) {
            N.decrementAndGet();
            return -1;
        }

        if(generationTime < SECOND_TENTH_TO_NANOS) {
            N.incrementAndGet();
            return 1;
        }

        return 0;
    }

    public boolean addBlock(Block newBlock) {
        if(isInvalid(newBlock)) return false;

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

    public synchronized void addTransaction(Transaction transaction) {
        boolean transactOkay = transactions.isEmpty() || transaction.getId() > transactions.last().getId();
        boolean tailOkay = this.tail == null || this.tail.checkMessages() || transaction.getId() > this.tail.getMessages().last().getId();
        boolean isTransactionValid = transactOkay && tailOkay;
        if(isTransactionValid) transactions.add(transaction);
    }

    public List<Transaction> getLastTransaction() {
        List<Transaction> lastMessages = new ArrayList<>(transactions);
        transactions.clear();
        return lastMessages;
    }

    public long getLastId() {
        return lastBlockId.incrementAndGet();
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

        public BlockItr(Block curr) {
            this.curr = curr;
        }

        @Override
        public boolean hasNext() {
            return curr != null;
        }

        @Override
        public Block next() {
            Block lastReturned = curr;
            curr = curr.next;
            return lastReturned;
        }
    }
}
