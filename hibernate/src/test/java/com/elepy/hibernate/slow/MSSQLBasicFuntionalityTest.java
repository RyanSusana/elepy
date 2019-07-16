package com.elepy.hibernate.slow;

import com.elepy.Configuration;
import com.elepy.hibernate.DatabaseConfigurations;
import com.elepy.tests.basic.BasicFuntionalityTest;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class MSSQLBasicFuntionalityTest extends BasicFuntionalityTest {

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
