package me.sebastian420.PandaAC;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LoggerThread implements Runnable {
    public static final Thread INSTANCE = new Thread(new LoggerThread(), "PandaACLogger");
    private static final Logger LOGGER = LogManager.getLogger("PandaAC");
    private static final BlockingQueue<String> INFO_QUEUE = new LinkedBlockingQueue<>();
    private static final BlockingQueue<String> WARN_QUEUE = new LinkedBlockingQueue<>();

    public static boolean running = true;

    static {
        INSTANCE.setDaemon(true);
    }

    private LoggerThread() {}

    @Override
    public void run() {
        while (running) {
            try {
                // Take from either queue, INFO_QUEUE first
                String message;
                if (!INFO_QUEUE.isEmpty()) {
                    message = INFO_QUEUE.take();
                    LOGGER.info(message);
                } else {
                    message = WARN_QUEUE.take();
                    LOGGER.warn(message);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Reset interrupted status
                break; // Exit loop if interrupted
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public static void info(String string) {
        INFO_QUEUE.add(string);
    }

    public static void warn(String string) {
        WARN_QUEUE.add(string);
    }
}


