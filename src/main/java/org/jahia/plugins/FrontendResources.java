package org.jahia.plugins;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.Map;

/**
 * Goal that executes a command and stores result in a file.
 *
 * @goal touch
 * @phase process-sources
 */
@Mojo(name = "frontend-resources")
public class FrontendResources extends AbstractMojo {
    public static final String errorFile = "commandErrorFile.txt";
    public static final String outputFile = "commandOutputFile.json";

    private static final Logger logger = LoggerFactory.getLogger(FrontendResources.class);
    private static final int timeout = 10000;

    @Parameter(defaultValue = "${basedir}", property = "workingDirectory", required = false)
    private File workingDirectory;

    @Parameter(defaultValue = "${basedir}", property = "outputDirectory", required = false)
    private File outputDirectory;

    @Parameter(defaultValue = "npm ls -json=true -prod=true -depth=0", property = "frontend-resources.command", required = false)
    private String command;

    @Parameter(property = "frontend-resources.additionalCommands", required = false)
    private Map<String, String> additionalCommands;

    public void execute() throws MojoExecutionException {
        File resources = new File(outputDirectory, outputFile);

        processCommand(resources, command, false);
        for (String key : additionalCommands.keySet()) {
            processCommand(resources, additionalCommands.get(key), true);

        }
    }

    private void processCommand(File resources, String command, boolean append) throws MojoExecutionException {
        Process p;
        try {
            ProcessBuilder pb = new ProcessBuilder(command.split("\\s"));
            p = pb.start();

            ReadErrorStreamProcess resp = new ReadErrorStreamProcess(p.getErrorStream(), new File(outputDirectory, "commandErrors.txt"));
            ReadInputStreamProcess risp = new ReadInputStreamProcess(p.getInputStream(), resources, append);

            if (resp.waitForStreamToBeRead(timeout) == ReadInputStreamProcess.TIMEOUT_CODE) {
                logger.error("Command execution time has timed out reading error stream!");
            }

            if (risp.waitForStreamToBeRead(timeout) == ReadInputStreamProcess.TIMEOUT_CODE) {
                logger.error("Command execution time has timed out reading input stream!");
            }

            ReadCommandProcess processWithTimeout = new ReadCommandProcess(p);
            int exitCode = processWithTimeout.waitForProcess(timeout);

            if (exitCode == ReadCommandProcess.TIMEOUT_CODE) {
                logger.error("Command execution time has timed out!!");
            } else if (exitCode != 0) {
                logger.error("Command failed to execute!!! See " + errorFile);
            }
            else {
                logger.info("Successfully executed command: " + command);
            }
            p.destroy();
        } catch (IOException e) {
            logger.error("Failed to write file. " + e.getCause().getMessage());
            logger.debug(Arrays.toString(e.getStackTrace()));
        }
    }
}
