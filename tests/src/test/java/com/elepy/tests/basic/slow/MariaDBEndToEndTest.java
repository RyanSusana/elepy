package com.elepy.tests.basic.slow;

import com.elepy.tests.basic.BasicEndToEndTest;
import com.elepy.tests.config.DatabaseConfigurations;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class MariaDBEndToEndTest extends BasicEndToEndTest {

    @Container
    private static final JdbcDatabaseContainer CONTAINER = new MariaDBContainer();

    public MariaDBEndToEndTest() {
        super(DatabaseConfigurations.createTestContainerConfiguration(
                CONTAINER,
                "org.hibernate.dialect.MariaDBDialect"
        ));
    }
}
