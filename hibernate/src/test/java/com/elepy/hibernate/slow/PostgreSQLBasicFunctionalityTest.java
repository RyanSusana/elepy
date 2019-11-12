package com.elepy.hibernate.slow;

import com.elepy.Configuration;
import com.elepy.Elepy;
import com.elepy.hibernate.DatabaseConfigurations;
import com.elepy.tests.basic.BasicFunctionalityTest;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class PostgreSQLBasicFunctionalityTest extends BasicFunctionalityTest {

    @Container
    private static final JdbcDatabaseContainer CONTAINER = new PostgreSQLContainer();

    @Override
    public void configureElepy(Elepy elepy) {
        elepy.addConfiguration(DatabaseConfigurations.createTestContainerConfiguration(
                CONTAINER,
                "org.hibernate.dialect.PostgreSQLDialect"
        ));
    }
}
