package org.jahia.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Reads a command in a timeoutable process
 *
 * @author alexander karmanov on 2016-10-21.
 */
public class ReadCommandProcess extends Thread {
    public static final int TIMEOUT_CODE = 408;

    private static final Logger logger = LoggerFactory.getLogger(ReadCommandProcess.class);

    private Process process;
    private int exitCode = TIMEOUT_CODE;

    public ReadCommandProcess(Process process) {
        this.process = process;
    }

    public int waitForProcess(int timeoutMilliseconds) {
        this.start();
        try {
            logger.debug("Start waiting ... "  + Integer.toString(timeoutMilliseconds));
            this.join(timeoutMilliseconds);
        } catch (InterruptedException e) {
            logger.debug("Interrupting after ... "  + Integer.toString(timeoutMilliseconds));
            this.interrupt();
        }
        return exitCode;
    }

    @Override
    public void run() {
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            logger.error("Process timed out. " + e.getCause().getMessage());
            logger.debug(Arrays.toString(e.getStackTrace()));
        } catch (Exception e) {
            logger.error("Things did dot go as planed executing your command. " + e.getCause().getMessage());
            logger.debug(Arrays.toString(e.getStackTrace()));
        }
    }
}
