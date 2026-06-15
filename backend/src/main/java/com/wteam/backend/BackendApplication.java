package com.wteam.backend;

import com.wteam.backend.system_listeners.PortAvailabilityListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

/**
 * The type Backend application.
 */
@SpringBootApplication
public class BackendApplication {

    /**
     * Main.
     *
     * @param args the args-d
     */
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication app = new SpringApplication(BackendApplication.class);
        app.addListeners(new PortAvailabilityListener());
        app.run(args);
    }

}
