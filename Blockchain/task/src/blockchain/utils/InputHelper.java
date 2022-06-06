package blockchain.utils;

import java.util.Scanner;

public class InputHelper {
    private static volatile boolean isClosed;
    private static Scanner scanner = new Scanner(System.in);
    public synchronized static String getMessage() {
        if(!isClosed) {
            String result;
            while ((result = scanner.nextLine()).isEmpty()) {}
            return result;
        }
        else throw new RuntimeException(
                InputHelper.class.getSimpleName().toLowerCase() + " is closed");
    }

    public static void off() {
        if(isClosed) return;
        isClosed = true;
        scanner.close();
    }
}
