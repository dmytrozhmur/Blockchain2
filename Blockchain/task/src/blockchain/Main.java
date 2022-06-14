package blockchain;

import blockchain.management.BlockChain;
import blockchain.management.Miner;
import blockchain.management.MoneyHandler;
import blockchain.management.User;
import blockchain.utils.InputHelper;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static blockchain.utils.Constants.BLOCKCHAIN_SIZE;
import static blockchain.utils.Constants.SECOND_TO_MILLIS;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        BlockChain blockChain = new BlockChain();
        List<User> users = List.of(
                new User("Dmytro", blockChain));
        List<MoneyHandler> moneyHandlers = new ArrayList<>(users);

        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            moneyHandlers.add(new Miner(blockChain));
        }
        blockChain.setMoneyHandlers(moneyHandlers);

        moneyHandlers.forEach(Thread::start);
        Thread.sleep(SECOND_TO_MILLIS);

        for (MoneyHandler handler : moneyHandlers) {
            handler.join();
        }
        InputHelper.off();
    }
}
