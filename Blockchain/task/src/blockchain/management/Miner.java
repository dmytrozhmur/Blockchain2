package blockchain.management;

import blockchain.exceptions.IllegalTransactionArgumentException;
import blockchain.exceptions.LoggingException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static blockchain.utils.Constants.*;
import static blockchain.utils.Encryption.getRandomNumber;

public class Miner extends MoneyHandler {
    private static final Logger errorLogger = Logger.getLogger("blockchain.management.Miner");
    private static final FileHandler handler;

    static {
        try {
            handler = new FileHandler("logger.txt");
            errorLogger.addHandler(handler);
            errorLogger.setLevel(Level.WARNING);
        } catch (IOException ioe) {
            throw new LoggingException(ioe.getMessage());
        }
    }

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
            } catch (RuntimeException re) {
                log(re.getMessage() + "\n");
            }
        }
    }

    private void log(String message) {
        errorLogger.warning(String
                    .format("%s : %s", nickName, message));
    }

    private Block generateBlock() {
        LocalTime before = LocalTime.now();

        long generatedYet = blockChain.getSize();
        Block newBlock = new Block();
        Block prevBlock = blockChain.getTail();

        do {
            newBlock.init(prevBlock);
        } while (!newBlock.isProved(blockChain.getN()));

        LocalTime after = LocalTime.now();
        long generationTime = after.toNanoOfDay() - before.toNanoOfDay();

        synchronized (blockChain) {
            if(generatedYet != blockChain.getSize()
                    || !blockChain.addBlock(newBlock)) return null;

            long size = blockChain.getSize();
            if(size > 1) newBlock.setMessages(blockChain.getLastTransaction());
            if(size <= BLOCKCHAIN_PRINTED_SIZE) printResult(newBlock, generationTime);
            return newBlock;
        }
    }

    private void printResult(Block block, long generationTime) {
        System.out.println("Block:\nCreated by " + nickName);
        System.out.println(nickName + " gets 100 VC");
        System.out.println("Id: " + block.getId());
        System.out.println("Timestamp: " + block.getTimeStamp());
        System.out.println("Magic number: " + block.getMagicNumber());
        System.out.println("Hash of the previous block:\n" + block.getPreviousHash());
        System.out.println("Hash of the block:\n" + block.hash());

        System.out.println("Block data:");
        if(block.checkMessages()) System.out.println(NO_TRANSACTIONS);
        else block.getMessages().forEach(System.out::println);

        System.out.printf("Block was generated for %f seconds\n", (double) generationTime / SECOND_TO_NANOS);

        int zerosDiff = blockChain.checkN(generationTime);
        if(zerosDiff == 1) System.out.println("N was increased to " + blockChain.getN());
        else if(zerosDiff == -1) System.out.println("N was decreased by 1");
        else System.out.println("N stays the same");
        System.out.println();
    }
}
