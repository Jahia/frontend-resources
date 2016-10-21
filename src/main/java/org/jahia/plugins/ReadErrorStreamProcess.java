package org.jahia.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Reads error stream
 *
 * @author alexander karmanov on 2016-10-21.
 */
public class ReadErrorStreamProcess extends ReadInputStreamProcess {

    private static final Logger logger = LoggerFactory.getLogger(ReadErrorStreamProcess.class);

    public ReadErrorStreamProcess(InputStream in, File file) {
        super(in, file, false);
    }

    @Override
    public void run() {
        FileWriter w = null;

        try {
            w = new FileWriter(file);
            String s;
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            while ((s = br.readLine()) != null) {
                w.write(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) +":"+s);
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
