package me.sebastian420.PandaAC.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PandaLogger {

    private static final Logger LOGGER = LogManager.getLogger("PandaAC");

    public static Logger getLogger() {
        return LOGGER;
    }

    /**
     * Logs error message to console.
     *
     * @param msg message (error) to log.
     */
    public static void logError(String msg) {
        LOGGER.error(msg);
    }
}
