package com.wteam.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * The type Backend application tests.
 */
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class BackendApplicationTests {

    /**
     * Context loads.
     */
    @Test
    void contextLoads() {
    }

}
