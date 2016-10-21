package org.jahia.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Tries to read input stream within a given timeout.
 *
 * @author alexander karmanov on 2016-10-21.
 */
public class ReadInputStreamProcess extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(ReadInputStreamProcess.class);

    public static final int TIMEOUT_CODE = 408;

    protected InputStream in;
    protected File file;
    private boolean append;
    protected int exitCode = TIMEOUT_CODE;

    public ReadInputStreamProcess(InputStream in, File file, boolean append) {
        this.in = in;
        this.file = file;
        this.append = append;
    }

    public int waitForStreamToBeRead(int timeoutMilliseconds) {
        this.start();
        try {
            logger.debug("Start reading stream "  + Integer.toString(timeoutMilliseconds));
            this.join(timeoutMilliseconds);
        } catch (InterruptedException e) {
            logger.debug("Interrupt reading stream "  + Integer.toString(timeoutMilliseconds));
            this.interrupt();
        }
        return exitCode;
    }

    @Override
    public void run() {
        FileWriter w = null;

        try {
            w = new FileWriter(file, append);
            String s;
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            while ((s = br.readLine()) != null) {
                w.write(s);
                w.write("\n");
            }
            br.close();
            in.close();
            exitCode = 0;
        } catch (IOException e) {
            exitCode = 1;
            logger.error("Failed to read input stream: " + e.getCause().getMessage());
        } finally {
            if (w != null) {
                try {
                    w.close();
                } catch (IOException e) {
                    logger.error("Filewriter did not close. " + e.getCause().getMessage());
                }
            }
        }
    }
}
