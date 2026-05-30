package com.wteam.backend.system_listeners;

import org.apache.commons.lang3.SystemUtils;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;

/**
 * The type Port availability listener.
 */
public class PortAvailabilityListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        Integer serverPort = event.getEnvironment().getProperty("server.port", Integer.class);
        if (serverPort == null) {
            throw new IllegalArgumentException("Set proper value for server.port in application.yaml file");
        }

        try (var _ = new ServerSocket(serverPort)) {
            System.out.printf("Port %d is free%n", serverPort);
        } catch (IOException e) {
            System.err.printf("Port %d is already in use%n", serverPort);

            String processInfo = findProcessUsingPort(serverPort);
            System.err.println("Process which using server port:\n" + processInfo);
            System.err.println("Please, stop this process or change server.port in application.yaml file\n");

            System.exit(1);
        }
    }

    private String findProcessUsingPort(int serverPort) {
        ProcessBuilder processBuilder;

        try {
            if (SystemUtils.IS_OS_WINDOWS) {
                processBuilder = new ProcessBuilder("cmd.exe", "/c", "netstat -ano | findstr :" + serverPort);
            } else {
                processBuilder = new ProcessBuilder("sh", "-c", "lsof -i :" + serverPort + " || netstat -nlp | grep :" + serverPort);
            }

            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                StringBuilder output = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.contains(":" + serverPort + " ")) {
                        output.append(" ").append(line.strip()).append("\n");
                    }
                }

                process.waitFor();

                if (!output.isEmpty()) {
                    return output.toString();
                } else {
                    return "Could not define the program. You may need administrator rights or the port is busy with the system service";
                }
            }
        } catch (Exception e) {
            return "Error while executing port checking";
        }
    }
}
