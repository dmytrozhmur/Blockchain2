package blockchain.management;

import blockchain.exceptions.IllegalTransactionArgumentException;
import blockchain.utils.InputHelper;

import static blockchain.utils.Constants.BLOCKCHAIN_SIZE;
import static blockchain.utils.Constants.NO_MESSAGES_BLOCKS_COUNT;
import static blockchain.utils.InputHelper.getMessage;

public class User extends MoneyHandler {

    public User(String name, BlockChain blockChain) {
        super(name, blockChain);
    }

    @Override
    public void run() {
        while (blockChain.getSize() < BLOCKCHAIN_SIZE) {
            try {
                createTransaction(getMessage().split(" "));
            } catch (IllegalTransactionArgumentException itae) {
                System.err.println(itae.getMessage());
            }
        }
    }

}
