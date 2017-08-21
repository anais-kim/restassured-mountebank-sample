package stub;

import org.mbtest.javabank.Client;
import util.CommandUtil;

import java.nio.file.Paths;
import java.util.concurrent.*;

public class Mountebank {

    private static final int CHECK_TIMEOUT = 5000;
    private static final int DEFAULT_PORT = 2525;

    private static Client client = new Client();

    public static void run() {
        CommandUtil.exec(Paths.get("."), "mb");
        checkAlive(true);
    }

    public static void stop() {
        String[] pidList = getPids(DEFAULT_PORT);
        for (String pid: pidList) {
            CommandUtil.exec(Paths.get("."), "kill", pid);
        }
        checkAlive(false);
    }

    private static String[] getPids(int port) {
        String output = CommandUtil.execOut(Paths.get("."), "lsof", "-t", "-i", ":"+port);
        return output.split(System.lineSeparator());
    }

    private static void checkAlive(boolean isAlive) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future future = executor.submit((Callable<Object>) () -> {
            // if want to check mountebank is alive.
            while (isAlive && !client.isMountebankRunning()) {
                TimeUnit.MILLISECONDS.sleep(100);
            }
            // if want to check mountebank is NOT alive.
            while (!isAlive && client.isMountebankRunning()) {
                TimeUnit.MILLISECONDS.sleep(100);
            }
            return true;
        });
        try {
            future.get(CHECK_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            String message = (isAlive)? "Mountebank is not alive." : "Mountebank is not stopped.";
            throw new RuntimeException(message);
        }
    }
}
