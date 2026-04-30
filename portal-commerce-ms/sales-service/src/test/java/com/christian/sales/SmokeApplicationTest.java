package com.christian.sales;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration"
})
class SmokeApplicationTest {

    @Test
    void contextLoads() {
        // context load smoke test with external integrations excluded
    }
}
