package blockchain.management;

import java.time.LocalTime;

import static blockchain.utils.Constants.*;
import static blockchain.utils.Encryption.getRandomNumber;

public class Miner extends MoneyHandler {
    public Miner(BlockChain blockChain) {
        super("miner", blockChain);
    }

    @Override
    public void run() {
        nickName += Thread.currentThread().getId();

        while (blockChain.getSize() < BLOCKCHAIN_SIZE) {
            if(generateBlock() != null) moneyForCreation += CREATION_AWARD;

            try {
                createTransaction(new String[] {
                        String.valueOf(getRandomNumber(getMoneyHeld()) + 1),
                        getRandomReceiver().toString()
                });
            } catch (RuntimeException re) {}
        }
    }

    private Block generateBlock() {
        LocalTime before = LocalTime.now();

        long generatedYet = blockChain.getSize();
        Block newBlock = new Block(nickName);
        Block prevBlock = blockChain.getTail();

        do {
            newBlock.init(blockChain.getN(), prevBlock);
        } while (!newBlock.isProved(blockChain.getN()));

        LocalTime after = LocalTime.now();
        long generationTime = after.toNanoOfDay() - before.toNanoOfDay();

        synchronized (blockChain) {
            if(generatedYet != blockChain.getSize()
                    || !blockChain.addBlock(newBlock)) return null;
            newBlock.setCreationTime((float) generationTime / SECONDTONANOS);
            newBlock.setZerosDiff(blockChain.checkN(generationTime));
            if(blockChain.getSize() > 1) newBlock.setMessages(blockChain.getLastTransaction());
            return newBlock;
        }

    }
}
