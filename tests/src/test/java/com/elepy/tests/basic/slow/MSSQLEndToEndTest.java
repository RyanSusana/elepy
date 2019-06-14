package com.elepy.tests.basic.slow;

import com.elepy.Configuration;
import com.elepy.tests.basic.EndToEndTest;
import com.elepy.tests.config.DatabaseConfigurations;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class MSSQLEndToEndTest extends EndToEndTest {

    @Container
    private static final MSSQLServerContainer MSSQL_SERVER_CONTAINER = new MSSQLServerContainer();

    @Override
    public Configuration configuration() {
        return DatabaseConfigurations.createTestContainerConfiguration(
                MSSQL_SERVER_CONTAINER,
                "org.hibernate.dialect.SQLServerDialect"
        );
    }
}
