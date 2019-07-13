package com.elepy.hibernate.slow;

import com.elepy.Configuration;
import com.elepy.hibernate.config.DatabaseConfigurations;
import com.elepy.tests.basic.BasicFuntionalityTest;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class MariaDBBasicFuntionalityTest extends BasicFuntionalityTest {

    @Container
    private static final JdbcDatabaseContainer CONTAINER = new MariaDBContainer();

    @Override
    public Configuration configuration() {
        return DatabaseConfigurations.createTestContainerConfiguration(
                CONTAINER,
                "org.hibernate.dialect.MariaDBDialect"
        );
    }
}
